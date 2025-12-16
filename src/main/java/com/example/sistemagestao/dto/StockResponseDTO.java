package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Stock;

public record StockResponseDTO(Long id, Long ingredientId, String ingredientName, Double quantity, String unitSymbol, String bakeryName) {
    public StockResponseDTO(Stock stock){
        this(
                stock.getId(),
                stock.getIngredient().getId(),
                stock.getIngredient().getName(),
                stock.getQuantity(),
                stock.getIngredient().getUnits().getSymbol(),
                stock.getBakery().getName()
        );
    }
}
