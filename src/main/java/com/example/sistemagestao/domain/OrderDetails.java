package com.example.sistemagestao.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "order_details",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"order_id", "product_id"})
        })
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    private Double price;

    private Integer discount;

    public OrderDetails(Order order, Product product, Integer quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }
}
