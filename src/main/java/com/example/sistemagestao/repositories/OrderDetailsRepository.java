package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.OrderDetails;
import com.example.sistemagestao.dto.STProductCostDTO;
import com.example.sistemagestao.dto.STProductSalesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    boolean existsByOrderIdAndProductId(Long orderId, Long productId);
    OrderDetails findByOrderIdAndProductId(Long orderId, Long productId);
    List<OrderDetails> findAllByOrderId(Long orderId);
    boolean existsByProductId(Long productId);
    int countAllByOrderId(Long orderId);


    @Query("""
        SELECT new com.example.sistemagestao.dto.STProductSalesDTO(
            od.product.name,
            SUM(od.quantity)
        )
        FROM OrderDetails od
        WHERE od.order.date BETWEEN :startDate AND :endDate
            AND od.order.orderState <> 'INCART'
            AND od.order.bakery.id = :bakeryId
        GROUP BY od.product.name
        ORDER BY SUM(od.quantity) DESC
    """)
    List<STProductSalesDTO> getProductSalesBetweenDates(
            @Param("bakeryId") Long bakeryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT new com.example.sistemagestao.dto.STProductCostDTO(
        od.product.name,
        SUM(
            (od.quantity * 1.0) * (od.price - (od.price * COALESCE(od.discount, 0) / 100.0))
        )
    )
    FROM OrderDetails od
    WHERE od.order.date BETWEEN :startDate AND :endDate
        AND od.order.orderState <> 'INCART'
        AND od.order.bakery.id = :bakeryId
    GROUP BY od.product.name
    ORDER BY SUM(
        (od.quantity * 1.0) * (od.price - (od.price * COALESCE(od.discount, 0) / 100.0))
    ) DESC
""")
    List<STProductCostDTO> getProductRevenueBetweenDates(
            @Param("bakeryId") Long bakeryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}
