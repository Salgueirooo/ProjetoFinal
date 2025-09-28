package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.RecipeIngredient;

public record RecipeIngredientResponseDTO(
        Long id,
        Long ingredientId,
        String name,
        Double quantity,
        String unitSymbol
) {
    public RecipeIngredientResponseDTO (RecipeIngredient recipeIngredient) {
        this(
            recipeIngredient.getId(),
            recipeIngredient.getIngredient().getId(),
            recipeIngredient.getIngredient().getName(),
            recipeIngredient.getQuantity(),
            recipeIngredient.getIngredient().getUnits().getSymbol()
        );
    }
}
