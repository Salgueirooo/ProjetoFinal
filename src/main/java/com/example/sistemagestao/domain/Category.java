package com.example.sistemagestao.domain;

import com.example.sistemagestao.dto.CategoryRequestDTO;
import com.example.sistemagestao.dto.ProductRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Table(name = "category")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String image;

    public Category (CategoryRequestDTO data) {
        this.name = data.name();
        this.image = data.image();
    }

    public void updateCategory(CategoryRequestDTO data) {
        if (data.name() != null) this.name = data.name();
        this.image = data.image();
    }

}
