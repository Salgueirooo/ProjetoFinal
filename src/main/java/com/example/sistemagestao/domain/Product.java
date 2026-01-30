package com.example.sistemagestao.domain;

import com.example.sistemagestao.dto.ProductRequestDTO;
import com.example.sistemagestao.repositories.ProductReviewRepository;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Table(name = "product")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    private String image;
    private String image_id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private Integer discount;
    private Boolean active;
    private Double rating;

    public Product(ProductRequestDTO data, Category category, String image, String image_id) {
        this.name = data.name();
        this.description = data.description();
        this.price = data.price();
        this.image = image;
        this.image_id = image_id;
        this.discount = data.discount() != null ? data.discount() : 0;
        this.active = data.active() != null ? data.active() : true;
        this.category = category;
        this.rating = 0.0;
    }

    public void updateProduct(ProductRequestDTO data, Category category, String image,  String image_id) {
        if (data.description() != null) this.description = data.description();
        if (data.price() != null) this.price = data.price();
        if (data.image() != null) this.image = image;
        if (image_id != null) this.image_id = image_id;
        if (data.discount() != null) this.discount = data.discount();
        if (category != null) this.category = category;
    }

    public void toggleActive() {
        this.active = !this.active;
    }
}
