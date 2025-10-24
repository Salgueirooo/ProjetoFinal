package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.OrderDetails;

public record OrderDetailsInCartResponseDTO(
        Long id,
        String productName,
        Integer quantity,
        Double price,
        Integer discount
) {
    public OrderDetailsInCartResponseDTO(OrderDetails orderDetails) {
        this(
                orderDetails.getId(),
                orderDetails.getProduct().getName(),
                orderDetails.getQuantity(),
                orderDetails.getProduct().getPrice(),
                orderDetails.getProduct().getDiscount()
        );
    }
}
