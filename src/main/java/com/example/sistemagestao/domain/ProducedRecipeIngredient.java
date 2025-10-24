package com.example.sistemagestao.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "produced_recipe_ingredient")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProducedRecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produced_recipe_id", nullable = false)
    private ProducedRecipe producedRecipe;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Boolean done;

    public ProducedRecipeIngredient(ProducedRecipe producedRecipe, Ingredient ingredient, Double quantity) {
        this.producedRecipe = producedRecipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.done = false;
    }

    public void toggleDone() {
        this.done = !this.done;
    }

}
