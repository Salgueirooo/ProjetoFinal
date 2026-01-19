package com.example.sistemagestao.dto;

import org.springframework.web.multipart.MultipartFile;

public record CategoryRequestDTO(String name, MultipartFile image) {
}
