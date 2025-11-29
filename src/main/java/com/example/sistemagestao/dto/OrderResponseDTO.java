package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Order;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        String userName,
        String phoneNumber,
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
                order.getUser().getPhone_number(),
                order.getDate(),
                order.getRequestDate(),
                order.getOrderState().getState(),
                order.getClientNotes(),
                order.getStaffNotes(),
                order.getOrderDetails().stream()
                        .sorted(Comparator.comparing(od -> od.getProduct().getName().toLowerCase()))
                        .map(OrderDetailsResponseDTO::new)
                        .toList()

        );
    }
}
