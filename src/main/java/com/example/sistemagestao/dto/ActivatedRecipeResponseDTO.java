package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.ProducedRecipe;

import java.time.LocalDateTime;
import java.util.List;

public record ActivatedRecipeResponseDTO(
        String productName,
        LocalDateTime initialDate,
        String userName,
        String preparation,
        List<ProducedRecipeIngredientResponseDTO> ingredientsList
        )
{
        public ActivatedRecipeResponseDTO(ProducedRecipe producedRecipe){
                this(
                        producedRecipe.getRecipe().getProduct().getName(),
                        producedRecipe.getInitialDate(),
                        producedRecipe.getUser().getName(),
                        producedRecipe.getRecipe().getPreparation(),
                        producedRecipe.getIngredientsList().stream().map(ProducedRecipeIngredientResponseDTO::new).toList()
                );
        }
}
