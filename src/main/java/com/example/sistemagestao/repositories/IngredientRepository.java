package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Bakery;
import com.example.sistemagestao.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
          AND s.quantity > :minQuantity
          AND s.bakery = :bakery
    )
""")
    List<Ingredient> findAllWithNoStockInBakery(
            @Param("minQuantity") Double minQuantity,
            @Param("bakery") Bakery bakery
    );

    boolean existsByName(String name);
}
