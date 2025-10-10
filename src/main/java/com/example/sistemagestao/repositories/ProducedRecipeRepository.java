package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.ProducedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProducedRecipeRepository extends JpaRepository <ProducedRecipe, Long> {
    List<ProducedRecipe> findByBakeryIdAndFinalDateIsNullOrderByInitialDateAsc(Long bakeryId);
    List<ProducedRecipe> findByBakeryIdOrderByInitialDateDesc(Long bakeryId);
    List<ProducedRecipe> findByBakeryId(Long bakeryId);
    List<ProducedRecipe> findAllByRecipeId(Long id);
}
