package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.ProductReview;

import java.time.LocalDateTime;

public record ProductReviewResponseDTO(
        Long id,
        LocalDateTime dateTime,
        String userName,
        Integer rating,
        String review
) {
    public ProductReviewResponseDTO(ProductReview review) {
        this(
                review.getId(),
                review.getDateTime(),
                review.getOrderDetails().getOrder().getUser().getName(),
                review.getRating(),
                review.getReview()
        );
    }
}
