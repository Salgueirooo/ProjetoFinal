package com.example.sistemagestao.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Roles {
    ADMIN("ADMIN", "Administrador"),
    CONFECTIONER("CONFECTIONER", "Pasteleiro"),
    COUNTER_EMPLOYEE("COUNTER_EMPLOYEE", "Empregado de Balcão"),
    CLIENT("COUNTER_EMPLOYEE", "Cliente");

    private String role;
    private String description;
}