package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.ProducedRecipeIngredient;

public record ProducedRecipeIngredientResponseDTO(
        String ingredientName,
        Double quantity,
        String unitSymbol,
        Boolean done
) {
    public ProducedRecipeIngredientResponseDTO(ProducedRecipeIngredient producedRecipeIngredient) {
        this(
                producedRecipeIngredient.getRecipeIngredient().getIngredient().getName(),
                producedRecipeIngredient.getRecipeIngredient().getQuantity(),
                producedRecipeIngredient.getRecipeIngredient().getIngredient().getUnits().getSymbol(),
                producedRecipeIngredient.getDone()
        );
    }
}
