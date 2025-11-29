package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Order;
import com.example.sistemagestao.domain.OrderStates;
import com.example.sistemagestao.dto.STMonthlyOrdersDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByBakery_Id(Long bakeryId);

    //carrinho
    Order findByUserIdAndBakery_IdAndOrderState(Long userId, Long bakeryId, OrderStates orderState);

    //todas as do utilizador exceto as do carrinho a partir de uma data
    List<Order> findAllByUserIdAndBakery_IdAndOrderStateNotInAndDateGreaterThanEqualOrderByDateAsc(Long userId, Long bakeryId, List<OrderStates> excludedState, LocalDateTime minDate);

    //todas as do utilizador exceto as do carrinho num certo dia
    List<Order> findAllByUserIdAndBakery_IdAndOrderStateNotAndDateBetweenOrderByDateAsc(Long userId, Long bakeryId, OrderStates excludedState, LocalDateTime minDate,  LocalDateTime maxDate);

    //todas as da pastelaria por estado da encomenda e data (aceites)
    List<Order> findAllByBakery_IdAndOrderStateAndDateBetweenOrderByDateAsc(Long bakeryId, OrderStates orderStates, LocalDateTime initialDate, LocalDateTime endDate);

    //todas as da pastelaria por estado da encomenda (pendentes)
    List<Order> findAllByBakery_IdAndOrderStateOrderByDateAsc(Long bakeryId, OrderStates orderStates);

    //todas as da pastelaria por nome do utilizador, sem as do carrinho por data
    List<Order> findAllByBakery_IdAndUser_EmailAndOrderStateNotAndDateBetweenOrderByDateAsc(Long bakeryId, String email, OrderStates excludedState, LocalDateTime initialDate, LocalDateTime endDate);

    //todas as da pastelaria por estado da encomenda, data e nome do utilizador
    List<Order> findAllByBakery_IdAndUserNameContainsIgnoreCaseAndOrderStateAndDateBetweenOrderByDateAsc(Long bakeryId, String userName, OrderStates orderStates, LocalDateTime initialDate, LocalDateTime endDate);

    @Query("""
        SELECT FUNCTION('MONTH', o.date) AS monthNumber,
               COUNT(o.id) AS totalOrders
        FROM OrderEntity o
        WHERE o.orderState = 'DELIVERED'
          AND o.bakery.id = :bakeryId
          AND FUNCTION('YEAR', o.date) = :year
        GROUP BY FUNCTION('MONTH', o.date)
        ORDER BY FUNCTION('MONTH', o.date)
    """)
    List<STMonthlyOrdersDTO> getMonthlyDeliveredOrdersByBakery(
            @Param("bakeryId") Long bakeryId,
            @Param("year") int year
    );

    @Query("""
        SELECT FUNCTION('MONTH', o.date) AS monthNumber,
               COUNT(o.id) AS totalOrders
        FROM OrderEntity o
        WHERE o.orderState = 'DELIVERED'
          AND o.bakery.id = :bakeryId
          AND o.user.id = :userId
          AND FUNCTION('YEAR', o.date) = :year
        GROUP BY FUNCTION('MONTH', o.date)
        ORDER BY FUNCTION('MONTH', o.date)
    """)
    List<STMonthlyOrdersDTO> getMonthlyDeliveredOrdersByUserAndBakery(
            @Param("userId") Long userId,
            @Param("bakeryId") Long bakeryId,
            @Param("year") int year
    );
}
