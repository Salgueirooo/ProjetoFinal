package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.OrderDetails;

public record OrderDetailsWReviewResponseDTO(
        Long id,
        Long productId,
        String productName,
        int quantity,
        Double price,
        Integer discount,
        boolean wasReviewed
) {
    public OrderDetailsWReviewResponseDTO(OrderDetails orderDetails, boolean wasReviewed) {
        this(
                orderDetails.getId(),
                orderDetails.getProduct().getId(),
                orderDetails.getProduct().getName(),
                orderDetails.getQuantity(),
                orderDetails.getPrice(),
                orderDetails.getDiscount(),
                wasReviewed
        );
    }
}
