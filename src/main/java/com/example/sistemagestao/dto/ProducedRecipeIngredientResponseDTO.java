package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.ProducedRecipeIngredient;

public record ProducedRecipeIngredientResponseDTO(
        Long id,
        String name,
        Double quantity,
        String unitSymbol,
        Boolean done
) {
    public ProducedRecipeIngredientResponseDTO(ProducedRecipeIngredient producedRecipeIngredient) {
        this(
                producedRecipeIngredient.getId(),
                producedRecipeIngredient.getIngredient().getName(),
                producedRecipeIngredient.getQuantity(),
                producedRecipeIngredient.getIngredient().getUnits().getSymbol(),
                producedRecipeIngredient.getDone()
        );
    }
}
