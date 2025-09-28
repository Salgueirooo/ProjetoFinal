package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.User;

public record UserResponseDTO(Long id, String name, String email, String role) {
    public UserResponseDTO (User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRole().getRole());
    }
}
