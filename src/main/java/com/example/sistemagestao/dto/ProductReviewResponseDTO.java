package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.ProductReview;

public record ProductReviewResponseDTO(
        Long id,
        String dateTime,
        String bakeryName,
        String userName,
        Integer rating,
        String review
) {
    public ProductReviewResponseDTO(ProductReview review) {
        this(
                review.getId(),
                review.getDateTime().toString().substring(0, 16).replace("T", " "),
                review.getOrderDetails().getOrder().getBakery().getName(),
                review.getOrderDetails().getOrder().getUser().getName(),
                review.getRating(),
                review.getReview()
        );
    }
}
