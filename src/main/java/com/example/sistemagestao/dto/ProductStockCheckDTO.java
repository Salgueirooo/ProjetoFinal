package com.example.sistemagestao.dto;

public record ProductStockCheckDTO(
        Long productId,
        String productName,
        int quantityNeeded,
        int availableQuantity,
        boolean sufficient
) {
}
