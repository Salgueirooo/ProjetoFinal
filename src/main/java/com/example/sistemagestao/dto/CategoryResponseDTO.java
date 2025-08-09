package com.example.sistemagestao.dto;


import com.example.sistemagestao.domain.Category;

public record CategoryResponseDTO(Long id, String name, String image) {
    public CategoryResponseDTO (Category category) {
        this(category.getId(), category.getName(), category.getImage());
    }
}
