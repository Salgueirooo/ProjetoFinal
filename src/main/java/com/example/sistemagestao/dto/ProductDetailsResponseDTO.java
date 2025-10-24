package com.example.sistemagestao.dto;

import java.util.List;

public record ProductDetailsResponseDTO(
        ProductResponseDTO product,
        List<ProductReviewResponseDTO> reviews
        ) {
}
