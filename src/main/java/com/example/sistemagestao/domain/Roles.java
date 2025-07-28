package com.example.sistemagestao.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Roles {
    ADMIN("Admin"),
    EMPLOYEE("Employee"),
    CLIENT("Client");

    private String description;


}