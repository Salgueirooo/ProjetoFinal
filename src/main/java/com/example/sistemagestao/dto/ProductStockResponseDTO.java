package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.ProductStock;

public record ProductStockResponseDTO(
        Long id,
        Long productId,
        String productName,
        int quantity,
        String bakeryName
) {
    public ProductStockResponseDTO(ProductStock productStock) {
        this(
                productStock.getId(),
                productStock.getProduct().getId(),
                productStock.getProduct().getName(),
                productStock.getQuantity(),
                productStock.getBakery().getName()
        );
    }
}
