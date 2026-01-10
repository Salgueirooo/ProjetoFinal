package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.ProducedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProducedRecipeRepository extends JpaRepository <ProducedRecipe, Long> {
    List<ProducedRecipe> findByBakeryIdAndFinalDateIsNullOrderByInitialDateAsc(Long bakeryId);
    List<ProducedRecipe> findByBakeryIdAndInitialDateBetweenOrderByInitialDateAsc(Long bakeryId, LocalDateTime startDate, LocalDateTime endDate);
    List<ProducedRecipe> findByBakeryIdAndInitialDateBetween(Long bakeryId, LocalDateTime startDate, LocalDateTime endDate);
    List<ProducedRecipe> findByBakeryId(Long bakeryId);
    //List<ProducedRecipe> findAllByRecipeId(Long id);
    boolean existsByRecipe_Product_Id(Long productId);
}
