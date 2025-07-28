package com.example.sistemagestao.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MeasurentUnits {
    KG("kg", "Quilogramas"),
    LITRE("L", "Litros"),
    UNITS("unidades", "Unidades");

    private String symbol;
    private String description;

    public static MeasurentUnits findByDescription(String description) {
        return Arrays.stream(values())
                .filter(unit -> unit.description.equalsIgnoreCase(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unidade inválida: " + description));
    }
}
