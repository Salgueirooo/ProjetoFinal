package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.User;
import com.example.sistemagestao.dto.*;
import com.example.sistemagestao.repositories.BakeryRepository;
import com.example.sistemagestao.repositories.OrderDetailsRepository;
import com.example.sistemagestao.repositories.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("api/statistics")
public class StatisticsController {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private BakeryRepository bakeryRepository;

    @GetMapping("/sales/{bakery_id}")
    public STAllSalesDTO getSales(
            @PathVariable Long bakery_id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        if(!bakeryRepository.existsById(bakery_id))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime data1 = startDate.atStartOfDay();
        LocalDateTime data2 = endDate.atTime(LocalTime.MAX);

        List<STProductSalesDTO> productSalesList = orderDetailsRepository
                .getProductSalesBetweenDates(bakery_id, data1, data2);

        STProductSalesDTO topProductSale = orderDetailsRepository
                .findTopProductBySalesBetweenDates(bakery_id, data1, data2, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        List<STClientSalesDTO> clientSalesList = orderDetailsRepository
                .getClientSalesBetweenDates(bakery_id, data1, data2);

        STClientSalesDTO topClientSale = orderDetailsRepository
                .findTopClientBySalesBetweenDates(bakery_id, data1, data2, PageRequest.of(0,1))
                .stream()
                .findFirst()
                .orElse(null);


        return new STAllSalesDTO(productSalesList, topProductSale, clientSalesList, topClientSale);
    }


    @GetMapping("/cost/{bakery_id}")
    public STAllCostDTO getCost(
            @PathVariable Long bakery_id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        if(!bakeryRepository.existsById(bakery_id))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime data1 = startDate.atStartOfDay();
        LocalDateTime data2 = endDate.atTime(LocalTime.MAX);

        List<STProductCostDTO> productCostList = orderDetailsRepository
                .getProductRevenueBetweenDates(bakery_id, data1, data2);

        STProductCostDTO topProductCost = orderDetailsRepository
                .findTopProductByRevenueBetweenDates(bakery_id, data1, data2, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        List<STClientSpendingDTO> clientSpendingList = orderDetailsRepository
                .getClientSpendingBetweenDates(bakery_id, data1, data2);

        STClientSpendingDTO topClientSpending = orderDetailsRepository
                .findTopClientBySpendingBetweenDates(bakery_id, data1, data2, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        return new STAllCostDTO(productCostList, topProductCost, clientSpendingList, topClientSpending);
    }

    // quantidade de encomendas em cada mes por um certo ano na pastelaria X
    @GetMapping("/orders-bakery/{bakery_id}")
    public List<STMonthlyOrdersResponseDTO> getOrdersBakery(
            @PathVariable Long bakery_id,
            @RequestParam int year
    ) {
        if(!bakeryRepository.existsById(bakery_id))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        if(year <= 0)
            throw new IllegalArgumentException("O Ano deve ser maior que zero.");

        List<STMonthlyOrdersDTO> monthlyOrdersList =
                orderRepository.getMonthlyDeliveredOrdersByBakery(bakery_id, year);

        Map<Integer, Long> totalsByMonth = monthlyOrdersList.stream()
                .collect(Collectors.toMap(STMonthlyOrdersDTO::getMonthNumber, STMonthlyOrdersDTO::getTotalOrders));

        List<STMonthlyOrdersResponseDTO> response = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            String monthName = getMonthName(i);
            Long total = totalsByMonth.getOrDefault(i, 0L);
            response.add(new STMonthlyOrdersResponseDTO(monthName, total));
        }

        return response;
    }

    // quantidade de encomendas em cada mes por um certo ano sobre o cliente
    @GetMapping("/orders-user/{bakery_id}")
    public List<STMonthlyOrdersResponseDTO> getOrdersUser(
            @PathVariable Long bakery_id,
            @RequestParam int year,
            @AuthenticationPrincipal User user
    ) {
        if(year <= 0)
            throw new IllegalArgumentException("O Ano deve ser maior que zero.");

        if(!bakeryRepository.existsById(bakery_id))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        List<STMonthlyOrdersDTO> monthlyOrdersList =
                orderRepository.getMonthlyDeliveredOrdersByUserAndBakery(user.getId(), bakery_id, year);

        Map<Integer, Long> totalsByMonth = monthlyOrdersList.stream()
                .collect(Collectors.toMap(STMonthlyOrdersDTO::getMonthNumber, STMonthlyOrdersDTO::getTotalOrders));

        List<STMonthlyOrdersResponseDTO> response = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            String monthName = getMonthName(i);
            Long total = totalsByMonth.getOrDefault(i, 0L);
            response.add(new STMonthlyOrdersResponseDTO(monthName, total));
        }

        return response;
    }


    private String getMonthName(int month) {
        return switch (month) {
            case 1 -> "Janeiro";
            case 2 -> "Fevereiro";
            case 3 -> "Março";
            case 4 -> "Abril";
            case 5 -> "Maio";
            case 6 -> "Junho";
            case 7 -> "Julho";
            case 8 -> "Agosto";
            case 9 -> "Setembro";
            case 10 -> "Outubro";
            case 11 -> "Novembro";
            case 12 -> "Dezembro";
            default -> "";
        };
    }
}
