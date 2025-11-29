package com.example.sistemagestao.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderWReviewResponseDTO(
        Long id,
        String userName,
        String phoneNumber,
        LocalDateTime date,
        LocalDateTime requestedDate,
        String orderState,
        String clientNotes,
        String staffNotes,
        List<OrderDetailsWReviewResponseDTO> orderDetails
) {}
