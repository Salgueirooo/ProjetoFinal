package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.SystemConfig;

public record VarsMakeOrderDTO(int minOrderHours, String openingTime, String closingTime) {
    public VarsMakeOrderDTO(SystemConfig minOrderHours, SystemConfig openingTime, SystemConfig closingTime) {
        this (Integer.parseInt(minOrderHours.getConfigValue()), openingTime.getConfigValue(), closingTime.getConfigValue());
    }
}