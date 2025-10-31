package com.example.sistemagestao.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")
@Entity(name = "OrderEntity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime requestDate;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStates orderState;

    private String clientNotes;
    private String staffNotes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetails> orderDetails = new ArrayList<>();

    public Order(User user, Bakery bakery) {
        this.user = user;
        this.bakery = bakery;
        this.orderState = OrderStates.INCART;
    }
}