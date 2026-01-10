package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByIngredientIdAndBakeryId(Long ingredientId, Long bakeryId);
    List<Stock> findAllByIngredientId(Long ingredientId);
    List<Stock> findAllByOrderByIngredientNameAscBakeryNameAsc();
    List<Stock> findAllByIngredientIdOrderByBakeryNameAsc(Long ingredientId);
    List<Stock> findAllByBakeryIdOrderByIngredientNameAsc(Long bakeryId);
    List<Stock> findByIngredientNameContainingIgnoreCaseOrderByIngredientNameAscBakeryNameAsc(String name);
    List<Stock> findByIngredientNameContainingIgnoreCaseAndBakeryIdOrderByIngredientNameAsc(String ingredientName, Long bakeryId);
    void deleteByBakeryId(Long bakeryId);
    boolean existsByIngredientIdAndQuantityGreaterThan(Long ingredientId, Double quantity);
    boolean existsByBakeryIdAndQuantityGreaterThan(Long bakeryId, Double quantity);
    List<Stock> findByBakeryIdAndIngredientIdIn(Long bakeryId, Set<Long> ingredientIds);
}
