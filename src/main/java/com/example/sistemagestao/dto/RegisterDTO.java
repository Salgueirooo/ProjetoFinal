package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Roles;

public record RegisterDTO(
        String name,
        String email,
        String password,
        String phone_number,
        Roles role
) {}
 