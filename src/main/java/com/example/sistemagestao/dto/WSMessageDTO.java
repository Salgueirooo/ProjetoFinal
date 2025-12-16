package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.Bakery;

import java.time.LocalDateTime;
import java.util.List;

public record WSMessageDTO(String message, Long bakeryId, String bakeryName, List<String> path, String hyperlink, String time) {
    public WSMessageDTO(String message, Bakery bakery, List<String> path, String hyperlink) {
        this (
            message,
            bakery == null ? null : bakery.getId(),
            bakery == null ? null : bakery.getName(),
            path,
            hyperlink,
            LocalDateTime.now().toString().substring(0, 19).replace("T", " ")
        );


    }
}
