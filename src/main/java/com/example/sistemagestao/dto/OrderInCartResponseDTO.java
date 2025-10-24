package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Order;

import java.util.List;
import java.util.stream.Collectors;

public record OrderInCartResponseDTO(
        Long id,
        List<OrderDetailsInCartResponseDTO> orderDetailsList
) {
    public OrderInCartResponseDTO (Order order) {
        this(
                order.getId(),
                order.getOrderDetails().stream().map(OrderDetailsInCartResponseDTO::new).toList()
        );
    }
}
