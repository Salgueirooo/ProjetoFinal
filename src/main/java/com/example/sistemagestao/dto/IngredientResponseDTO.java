package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Ingredient;
import com.example.sistemagestao.domain.MeasurentUnits;

public record IngredientResponseDTO (
        Long id,
        String name,
        String unitSymbol,
        String unitDescription
){
    public IngredientResponseDTO (Ingredient ingredient) {
        this(
            ingredient.getId(),
            ingredient.getName(),
            ingredient.getUnits().getSymbol(),
            ingredient.getUnits().getDescription()
        );
    }
}
