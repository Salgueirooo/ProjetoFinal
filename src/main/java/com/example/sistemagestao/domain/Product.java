package com.example.sistemagestao.domain;

import com.example.sistemagestao.dto.ProductRequestDTO;
import jakarta.persistence.*;
import lombok.*;

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

    private String description;

    @Column(nullable = false)
    private Double price;

    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Integer iva;
    private Integer discount;
    private Boolean active;

    public Product(ProductRequestDTO data, Category category) {
        this.name = data.name();
        this.description = data.description();
        this.price = data.price();
        this.image = data.image();
        this.iva = data.iva() != null ? data.iva() : 0;
        this.discount = data.discount() != null ? data.discount() : 0;
        this.active = data.active() != null ? data.active() : true;
        this.category = category;
    }

    public void updateProduct(ProductRequestDTO data, Category category) {
        if (data.name() != null) this.name = data.name();
        this.description = data.description();
        if (data.price() != null) this.price = data.price();
        this.image = data.image();
        if (data.iva() != null) this.iva = data.iva();
        if (data.discount() != null) this.discount = data.discount();
        if (data.active() != null) this.active = data.active();
        if (category != null) this.category = category;
    }

    public void toggleActive() {
        this.active = !this.active;
    }
}
