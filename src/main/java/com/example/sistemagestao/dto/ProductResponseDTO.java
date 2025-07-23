package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Product;

public record ProductResponseDTO(Long id, String name, String description, Double price, String image, Integer iva, Integer discount, String categoryName) {
    public ProductResponseDTO (Product product) {
        this(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getImage(),
            product.getIva(),
            product.getDiscount(),
            product.getCategory() != null ? product.getCategory().getName() : null
        );
    }
}
