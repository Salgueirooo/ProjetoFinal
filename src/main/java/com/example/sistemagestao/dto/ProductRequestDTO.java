package com.example.sistemagestao.dto;

public record ProductRequestDTO(String name, String description, Double price, String image, String category, Integer iva, Integer discount) {
}
