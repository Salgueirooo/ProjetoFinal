package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeIngredientsRepository extends JpaRepository<RecipeIngredient, Long> {
    List<RecipeIngredient> findAllByRecipeId(Long recipeId);
    List<RecipeIngredient> findAllByIngredientId(Long ingredientId);

    boolean existsByRecipeIdAndIngredientId(Long recipeId, Long ingredientId);
}
