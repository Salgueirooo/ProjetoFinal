package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByOrderByProductNameAsc();
    List<Recipe> findByProductNameContainingIgnoreCaseOrderByProductNameAsc(String name);

    boolean existsByProductId(Long productId);
}
