package com.example.sistemagestao.dto;

import org.springframework.web.multipart.MultipartFile;

public record BakeryRequestDTO(
        String name,
        MultipartFile logo,
        String phone_number,
        String email,
        String address
) {}
