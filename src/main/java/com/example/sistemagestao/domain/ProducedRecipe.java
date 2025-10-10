package com.example.sistemagestao.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "produced_recipe")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProducedRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "bakery_id", nullable = false)
    private Bakery bakery;

    @Column(nullable = false)
    private LocalDateTime initialDate;

    private LocalDateTime finalDate;

    @OneToMany(mappedBy = "producedRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProducedRecipeIngredient> ingredientsList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ProducedRecipe(Recipe recipe, Bakery bakery, User user) {
        this.recipe = recipe;
        this.bakery = bakery;
        this.initialDate = LocalDateTime.now();
        this.finalDate = null;
        this.user = user;
    }
}
