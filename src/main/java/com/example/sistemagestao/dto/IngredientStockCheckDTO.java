package com.example.sistemagestao.dto;

public record IngredientStockCheckDTO(
        IngredientResponseDTO ingredient,
        Double quantityNeeded,
        Double availableQuantity,
        boolean sufficient
) {}
