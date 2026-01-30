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
    private String image_id;

    public Category (CategoryRequestDTO data, String image, String imageId) {
        this.name = data.name();
        this.image = image;
        this.image_id = imageId;
    }

    public void updateCategory(String image, String imageId) {
        if (image != null) this.image = image;
        if (imageId != null) this.image_id = imageId;
    }

}
