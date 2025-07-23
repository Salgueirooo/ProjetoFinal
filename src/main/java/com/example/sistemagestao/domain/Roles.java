package com.example.sistemagestao.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Roles {
    ADMIN(1, "admin"),
    EMPLOYEE(2, "employee"),
    CLIENT(3, "client");

    private int id;
    private String description;


}
