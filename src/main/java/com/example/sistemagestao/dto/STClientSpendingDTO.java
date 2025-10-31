package com.example.sistemagestao.dto;

import java.math.BigDecimal;

public record STClientSpendingDTO(String clientName, BigDecimal totalSpent) {
    public STClientSpendingDTO(String clientName, Number totalSpent) {
        this (
                clientName,
                totalSpent == null ? BigDecimal.ZERO : new BigDecimal(totalSpent.toString())
        );
    }
}
