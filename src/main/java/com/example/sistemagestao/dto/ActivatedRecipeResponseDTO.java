package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.ProducedRecipe;

import java.time.LocalDateTime;
import java.util.List;

public record ActivatedRecipeResponseDTO(
        Long id,
        String productName,
        String productImage,
        LocalDateTime initialDate,
        String userName,
        String preparation,
        Double dose,
        List<ProducedRecipeIngredientResponseDTO> ingredientsList
        )
{
        public ActivatedRecipeResponseDTO(ProducedRecipe producedRecipe){
                this(
                        producedRecipe.getId(),
                        producedRecipe.getRecipe().getProduct().getName(),
                        producedRecipe.getRecipe().getProduct().getImage(),
                        producedRecipe.getInitialDate(),
                        producedRecipe.getUser().getName(),
                        producedRecipe.getPreparation(),
                        producedRecipe.getDose(),
                        producedRecipe.getIngredientsList().stream().map(ProducedRecipeIngredientResponseDTO::new).toList()
                );
        }
}
