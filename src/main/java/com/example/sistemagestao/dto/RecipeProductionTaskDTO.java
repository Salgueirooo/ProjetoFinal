package com.example.sistemagestao.dto;

public record RecipeProductionTaskDTO(
        Long recipeId,
        String recipeName,
        double requiredDoses,
        Long totalProducts,
        double producedDoses
) {}
