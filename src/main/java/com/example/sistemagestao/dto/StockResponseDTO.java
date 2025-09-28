package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Stock;

public record StockResponseDTO(String name, Double quantity, String unitSymbol, String bakeryName) {
    public StockResponseDTO(Stock stock){
        this(
                stock.getIngredient().getName(),
                stock.getQuantity(),
                stock.getIngredient().getUnits().getSymbol(),
                stock.getBakery().getName()
        );
    }
}
