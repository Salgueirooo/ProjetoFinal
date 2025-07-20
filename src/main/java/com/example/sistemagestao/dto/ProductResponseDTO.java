package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Product;

public record ProductResponseDTO(Long id, String name, String description, Double price, String image, String category, Integer iva, Integer discount) {
    public ProductResponseDTO (Product product) {
        this(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getImage(), product.getCategory(), product.getIva(), product.getDiscount());
    }
}
