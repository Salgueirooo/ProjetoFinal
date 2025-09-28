package com.example.sistemagestao.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(
        name = "stock",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ingredient_id", "bakery_id"})
        }
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @ManyToOne
    @JoinColumn(name = "bakery_id", nullable = false)
    private Bakery bakery;

    private Double quantity;

    public Stock(Ingredient ingredient, Bakery bakery, Double quantity) {
        this.ingredient = ingredient;
        this.bakery = bakery;
        this.quantity = quantity;
    }
}
