package com.example.sistemagestao.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(
        name = "product_stock",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_id", "bakery_id"})
        }
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProductStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "bakery_id", nullable = false)
    private Bakery bakery;

    private int quantity;

    public ProductStock(Product product, Bakery bakery, int quantity) {
        this.product = product;
        this.bakery = bakery;
        this.quantity = quantity;
    }
}
