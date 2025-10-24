package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        String userName,
        LocalDateTime date,
        LocalDateTime requestedDate,
        String orderState,
        String clientNotes,
        String staffNotes,
        List<OrderDetailsResponseDTO> orderDetails
) {
    public OrderResponseDTO(Order order) {
        this(
                order.getId(),
                order.getUser().getName(),
                order.getDate(),
                order.getRequestDate(),
                order.getOrderState().getState(),
                order.getClientNotes(),
                order.getStaffNotes(),
                order.getOrderDetails().stream().map(OrderDetailsResponseDTO::new).toList()

        );
    }
}
