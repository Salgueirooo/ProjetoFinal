package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.ProducedRecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProducedRecipeIngredientRepository extends JpaRepository <ProducedRecipeIngredient, Long> {
    //List<ProducedRecipeIngredient> findAllByProducedRecipe_Recipe_IdAndIngredient_Id(Long recipeId, Long ingredientId);
    List<ProducedRecipeIngredient> findAllByProducedRecipe_Id(Long producedRecipeId);
    boolean existsByIngredientId(Long ingredientId);
}
