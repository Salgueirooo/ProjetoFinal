package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findAllByOrderByNameAsc();
    List<Ingredient> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    @Query("""
        SELECT i
        FROM Ingredient i
        WHERE NOT EXISTS (
            SELECT s
            FROM Stock s
            WHERE s.ingredient = i
              AND s.quantity > 0
        )
    """)
    List<Ingredient> findAllWithNoStockInAnyBakery();

    boolean existsByName(String name);
}
