package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Recipe;

import java.util.List;

public record RecipeResponseDTO(Long id, String productName, String preparation, List<RecipeIngredientResponseDTO> ingredients) {
    public RecipeResponseDTO (Recipe recipe) {
        this(
            recipe.getId(),
            recipe.getProduct().getName(),
            recipe.getPreparation(),
            recipe.getIngredientsList().stream()
                .map(RecipeIngredientResponseDTO::new)
                .toList()
        );
    }
}
