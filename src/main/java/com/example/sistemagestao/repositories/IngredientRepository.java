package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Ingredient;
import com.example.sistemagestao.dto.IngredientResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findAllByOrderByNameAsc();
    List<Ingredient> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
