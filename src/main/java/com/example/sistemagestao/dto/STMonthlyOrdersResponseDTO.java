package com.example.sistemagestao.dto;

public record STMonthlyOrdersResponseDTO(String monthName, Long totalOrders) {
    public STMonthlyOrdersResponseDTO (String monthName, Long totalOrders) {
        this.monthName = monthName;
        this.totalOrders = totalOrders;
    }
}
