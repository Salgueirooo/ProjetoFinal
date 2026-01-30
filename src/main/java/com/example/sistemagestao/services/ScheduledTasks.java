package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Bakery;
import com.example.sistemagestao.domain.Order;
import com.example.sistemagestao.domain.OrderStates;
import com.example.sistemagestao.dto.IngredientStockCheckDTO;
import com.example.sistemagestao.repositories.BakeryRepository;
import com.example.sistemagestao.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private StockService stockService;
    @Autowired
    private BakeryRepository bakeryRepository;
    @Autowired
    private SystemConfigService systemConfigService;

    @Scheduled(cron = "0 59 23 * * *", zone = "Europe/Lisbon")
    @Transactional
    public void closeExpiredOrders() {
        LocalDateTime limitDate = LocalDateTime.now(ZoneId.of("Europe/Lisbon"))
                .minusHours(24);

        List<Order> expiredOrders =
                orderRepository.findAllByOrderStateAndDateLessThanEqual(
                        OrderStates.READY,
                        limitDate
                );

        for (Order order : expiredOrders) {
            order.setOrderState(OrderStates.NOTDELIVERED);
        }

        orderRepository.saveAll(expiredOrders);
    }

    @Scheduled(cron = "0 00 09 * * *", zone = "Europe/Lisbon")
    @Transactional
    public void getIngredientsStatus() {
        List<Bakery> bakeries = bakeryRepository.findAll();

        int daysToCheck = Integer.parseInt(systemConfigService.getVar("CHECK_STOCK_DAYS"));
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysToCheck);


        for (Bakery bakery : bakeries) {
            List<IngredientStockCheckDTO> stockChecks = stockService.checkStock(bakery.getId(), startDate, endDate);

            String report;

            if (stockChecks.isEmpty()) {
                report = "Relatório de Ingredientes - Nenhum produto foi encomendado neste intervalo.";
            } else {
                List<IngredientStockCheckDTO> insufficient = stockChecks.stream()
                        .filter(i -> !i.sufficient())
                        .toList();

                if (insufficient.isEmpty()) {
                    report = "Relatório de Ingredientes - Todos os ingredientes têm quantidade suficiente.";
                } else {
                    String missingIngredients = insufficient.stream()
                            .map(i -> i.ingredient().name())
                            .collect(Collectors.joining(", "));
                    report = "Relatório de Ingredientes - Existem ingredientes com quantidade insuficiente: " + missingIngredients + ".";
                }
            }

            notificationService.sendToRole("ROLE_ADMIN", report, null, bakery, null);
        }
    }
}
