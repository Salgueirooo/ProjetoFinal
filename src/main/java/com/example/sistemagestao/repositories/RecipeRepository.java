package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Recipe;
import com.example.sistemagestao.dto.STRecipeIngredientDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByOrderByProductNameAsc();
    List<Recipe> findByProductNameContainingIgnoreCaseOrderByProductNameAsc(String name);
    List<Recipe> findByProduct_ActiveTrueOrderByProductNameAsc();
    List<Recipe> findByProduct_ActiveTrueAndProductNameContainingIgnoreCaseOrderByProductNameAsc(String name);
    Optional<Recipe> findByProduct_id(Long productId);
    boolean existsByProductId(Long productId);

    @Query("""
        SELECT new com.example.sistemagestao.dto.STRecipeIngredientDTO(
            p.id,
            i.id,
            i.name,
            ri.quantity,
            r.id,
            r.nResultingProducts
        )
        FROM Recipe r
            JOIN r.product p
            JOIN r.ingredientsList ri
            JOIN ri.ingredient i
        WHERE r.product.id IN :productIds
    """)
    List<STRecipeIngredientDTO> getIngredientsForProducts(
            @Param("productIds") List<Long> productIds
    );

    List<Recipe> findByIdInOrderByProduct_NameAsc(Collection<Long> ids);
    List<Recipe> findByProduct_IdIn(Collection<Long> productIds);
}
