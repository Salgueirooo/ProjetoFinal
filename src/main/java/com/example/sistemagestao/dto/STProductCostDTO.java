package com.example.sistemagestao.dto;

import lombok.Getter;

import java.math.BigDecimal;

public record STProductCostDTO (String productName, BigDecimal totalRevenue) {
    public STProductCostDTO(String productName, Number totalRevenue) {
        this (
                productName,
                totalRevenue == null ? BigDecimal.ZERO : new BigDecimal(totalRevenue.toString())
        );
    }
}
