package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Bakery;

public record BakeryResponseDTO(
        Long id,
        String name,
        String logo,
        String phone_number,
        String email,
        String address
) {
    public BakeryResponseDTO(Bakery bakery) {
        this (
                bakery.getId(),
                bakery.getName(),
                bakery.getLogo(),
                bakery.getPhone_number(),
                bakery.getEmail(),
                bakery.getAddress()
        );
    }
}
