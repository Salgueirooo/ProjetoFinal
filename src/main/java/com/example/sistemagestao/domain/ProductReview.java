package com.example.sistemagestao.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "product_reviews")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProductReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_details_id", unique = true)
    private OrderDetails orderDetails;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private Integer rating;

    private String review;

    public ProductReview(OrderDetails orderDetails, Integer rating, String review) {
        this.orderDetails = orderDetails;
        this.rating = rating;
        this.review = review;
        this.dateTime = LocalDateTime.now();
    }
}
