package com.example.sistemagestao.dto;

public record STRecipeIngredientDTO(
        Long productId,
        Long ingredientId,
        String ingredientName,
        Double ingredientQuantityPerRecipe,
        Long recipeId,
        Integer recipeYield
) {}
