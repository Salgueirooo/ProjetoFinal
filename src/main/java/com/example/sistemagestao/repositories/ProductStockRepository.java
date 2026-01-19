package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    ProductStock findByProductIdAndBakeryId(Long productId, Long bakeryId);
    List<ProductStock> findAllByBakeryIdOrderByProductNameAsc(Long bakeryId);
    boolean existsByBakeryIdAndQuantityGreaterThan(Long bakeryId, int quantity);
    void deleteByBakeryId(Long bakeryId);
}
