package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.OrderDetails;

public record OrderDetailsResponseDTO(
        Long id,
        String productName,
        int quantity,
        Double price,
        Integer discount
) {
    public OrderDetailsResponseDTO (OrderDetails orderDetails) {
        this(
                orderDetails.getId(),
                orderDetails.getProduct().getName(),
                orderDetails.getQuantity(),
                orderDetails.getPrice(),
                orderDetails.getDiscount()
        );
    }
}
