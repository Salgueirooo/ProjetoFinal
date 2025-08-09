package com.example.sistemagestao.dto;

public record RecipeIngredientRequestDTO(Long recipeId, Long ingredientId, Double quantity) {
}
