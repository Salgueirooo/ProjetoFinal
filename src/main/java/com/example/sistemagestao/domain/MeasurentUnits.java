package com.example.sistemagestao.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum MeasurentUnits {
    KG("kg", "Quilogramas"),
    LITRE("L", "Litros"),
    UNITS("un.", "Unidades");

    private final String symbol;
    private final String description;

    public static MeasurentUnits findByDescription(String description) {
        return Arrays.stream(values())
                .filter(unit -> unit.description.equalsIgnoreCase(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unidade inválida: " + description));
    }
}
