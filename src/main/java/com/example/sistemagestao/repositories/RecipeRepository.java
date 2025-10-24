package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByOrderByProductNameAsc();
    List<Recipe> findByProductNameContainingIgnoreCaseOrderByProductNameAsc(String name);
    List<Recipe> findByProduct_ActiveTrueOrderByProductNameAsc();
    List<Recipe> findByProduct_ActiveTrueAndProductNameContainingIgnoreCaseOrderByProductNameAsc(String name);
    Optional<Recipe> findByProduct_id(Long productId);
    boolean existsByProductId(Long productId);
}
