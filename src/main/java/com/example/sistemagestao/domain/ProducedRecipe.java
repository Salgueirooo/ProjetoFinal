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
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String preparation;

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

    public ProducedRecipe(Product product, Bakery bakery, String preparation, User user) {
        this.product = product;
        this.bakery = bakery;
        this.preparation = preparation;
        this.initialDate = LocalDateTime.now();
        this.finalDate = null;
        this.user = user;
    }
}
