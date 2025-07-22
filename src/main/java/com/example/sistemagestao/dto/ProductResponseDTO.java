package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Product;

public record ProductResponseDTO(Long id, String name, String description, Double price, String image, Integer iva, Integer discount, Long categoryId, String cat) {
    public ProductResponseDTO (Product product) {
        this(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getImage(), product.getIva(), product.getDiscount(), product.getCategory().getId(), product.getCategory().getName());
    }
}
