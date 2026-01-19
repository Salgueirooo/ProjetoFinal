package com.example.sistemagestao.dto;

import org.springframework.web.multipart.MultipartFile;

public record ProductRequestDTO(
        String name,
        String description,
        Double price,
        MultipartFile image,
        Long categoryId,
        Integer iva,
        Integer discount,
        Boolean active
) {
}
