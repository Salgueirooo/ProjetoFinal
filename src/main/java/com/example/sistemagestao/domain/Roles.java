package com.example.sistemagestao.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Roles {
    ADMIN("Administrador"),
    COUNTER_EMPLOYEE("Empregado de Balcão"),
    CONFECTIONER("Pasteleiro"),
    CLIENT("Cliente");

    private String role;


}