package com.example.sistemagestao.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStates {

    INCART("No carrinho"),
    PENDING("Pendente"),
    CANCELLED("Cancelada"),
    ACCEPTED("Aceite"),
    REJECTED("Recusada"),
    READY("Pronta"),
    DELIVERED("Entregue");

    private final String state;
}
