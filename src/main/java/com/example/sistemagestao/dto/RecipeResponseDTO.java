package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Recipe;

import java.util.List;

public record RecipeResponseDTO(
        Long id,
        Long productId,
        String productName,
        String image,
        String preparation,
        int nResultingProducts,
        List<RecipeIngredientResponseDTO> ingredients
) {
    public RecipeResponseDTO (Recipe recipe) {
        this(
            recipe.getId(),
            recipe.getProduct().getId(),
            recipe.getProduct().getName(),
            recipe.getProduct().getImage(),
            recipe.getPreparation(),
            recipe.getNResultingProducts(),
            recipe.getIngredientsList().stream()
                .map(RecipeIngredientResponseDTO::new)
                .toList()
        );
    }
}
