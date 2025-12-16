package com.example.sistemagestao.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Roles {
    ADMIN("ADMIN", "Administrador"),
    CONFECTIONER("CONFECTIONER", "Pasteleiro"),
    COUNTER_EMPLOYEE("COUNTER_EMPLOYEE", "Empregado de Balcão"),
    CLIENT("CLIENT", "Cliente");

    private final String role;
    private final String description;
}