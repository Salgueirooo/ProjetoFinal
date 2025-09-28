package com.example.sistemagestao.dto;

public record ProductRequestDTO(
        String name,
        String description,
        Double price, String image,
        Long categoryId,
        Integer iva,
        Integer discount,
        Boolean active
) {
}
