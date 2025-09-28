package com.example.sistemagestao.dto;

public record IngredientStockCheckDTO(
        RecipeIngredientResponseDTO ingredient,
        Double availableQuantity,
        boolean sufficient
) {}
