package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Product;

public record RecipeRequestDTO(Long productId, String description) {
}
