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
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    private String image;

    //@Column(nullable = false)
    private String category;

    private Integer iva;
    private Integer discount;

    public Product(ProductRequestDTO data) {
        this.name = data.name();
        this.description = data.description();
        this.price = data.price();
        this.image = data.image();
        this.category = data.category();
        this.iva = data.iva();
        this.discount = data.discount();
    }

    public void updateProduct(ProductRequestDTO data) {
        if (data.name() != null) this.name = data.name();
        this.description = data.description();
        if (data.price() != null) this.price = data.price();
        this.image = data.image();
        if (data.category() != null) this.category = data.category();
        this.iva = data.iva();
        this.discount = data.discount();
    }

}
