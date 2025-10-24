package com.example.sistemagestao.dto;

import java.time.LocalDateTime;

public record OrderRequestDTO(
        Long id,
        String clientNotes,
        LocalDateTime date
) {
}
