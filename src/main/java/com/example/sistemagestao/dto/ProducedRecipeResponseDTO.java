package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.ProducedRecipe;

import java.time.LocalDateTime;

public record ProducedRecipeResponseDTO(
        Long id,
        String productName,
        LocalDateTime initialDate,
        LocalDateTime finalDate,
        String userName)
{
    public ProducedRecipeResponseDTO(ProducedRecipe producedRecipe) {
        this(
                producedRecipe.getId(),
                producedRecipe.getRecipe().getProduct().getName(),
                producedRecipe.getInitialDate(),
                producedRecipe.getFinalDate(),
                producedRecipe.getUser().getName()
        );
    }
}
