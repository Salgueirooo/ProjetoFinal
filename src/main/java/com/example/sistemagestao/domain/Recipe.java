package com.example.sistemagestao.domain;

import com.example.sistemagestao.dto.RecipeRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "recipe")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(columnDefinition = "TEXT")
    private String preparation;

    @Column(nullable = false)
    private int nResultingProducts;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> ingredientsList = new ArrayList<>();

    public Recipe(Product product, String preparation,  int nResultingProducts) {
        this.product = product;
        this.preparation = preparation;
        this.nResultingProducts = nResultingProducts;
    }

    public void upgradeRecipe(RecipeRequestDTO recipeRequest) {
        if (recipeRequest.nResultingProducts() > 0) this.nResultingProducts = recipeRequest.nResultingProducts();
        if (!recipeRequest.preparation().isEmpty()) this.preparation = recipeRequest.preparation();
    }
}
