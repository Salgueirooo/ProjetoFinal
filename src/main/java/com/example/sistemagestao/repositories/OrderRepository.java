package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Order;
import com.example.sistemagestao.domain.OrderStates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByBakery_Id(Long bakeryId);

    //carrinho
    Order findByUserIdAndBakery_IdAndOrderState(Long userId, Long bakeryId, OrderStates orderState);

    //todas as do utilizador menos as do carrinho
    List<Order> findAllByUserIdAndBakery_IdAndOrderStateNotOrderByDateDesc(Long userId, Long bakeryId, OrderStates excludedState);

    //todas as da pastelaria por estado da encomenda e data (aceites)
    List<Order> findAllByBakery_IdAndOrderStateAndDateBetweenOrderByDateAsc(Long bakeryId, OrderStates orderStates, LocalDateTime initialDate, LocalDateTime endDate);

    //todas as da pastelaria por estado da encomenda (pendentes)
    List<Order> findAllByBakery_IdAndOrderStateOrderByRequestDateAsc(Long bakeryId, OrderStates orderStates);

    //todas as da pastelaria por nome do utilizador, sem as do carrinho por data
    List<Order> findAllByBakery_IdAndUserNameContainsIgnoreCaseAndOrderStateNotAndDateBetweenOrderByDateAsc(Long bakeryId, String userName, OrderStates excludedState, LocalDateTime initialDate, LocalDateTime endDate);

    //todas as da pastelaria por estado da encomenda, data e nome do utilizador
    List<Order> findAllByBakery_IdAndUserNameContainsIgnoreCaseAndOrderStateAndDateBetweenOrderByDateAsc(Long bakeryId, String userName, OrderStates orderStates, LocalDateTime initialDate, LocalDateTime endDate);
}
