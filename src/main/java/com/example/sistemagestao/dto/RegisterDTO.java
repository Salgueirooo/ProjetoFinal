package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Roles;

public record RegisterDTO(
        String name,
        String email,
        String password,
        Roles role,
        String phone_number
) {}
 