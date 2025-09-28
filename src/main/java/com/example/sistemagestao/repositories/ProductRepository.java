package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByNameAsc();
    List<Product> findByActiveTrueOrderByNameAsc();
    List<Product> findByActiveFalseOrderByNameAsc();

    List<Product> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
    List<Product> findByNameContainingIgnoreCaseAndActiveTrueOrderByNameAsc(String name);
    List<Product> findByNameContainingIgnoreCaseAndActiveFalseOrderByNameAsc(String name);

    List<Product> findByCategoryIdOrderByNameAsc(Long categoryId);
    List<Product> findByCategoryIdAndActiveTrueOrderByNameAsc(Long categoryId);
    List<Product> findByCategoryIdAndActiveFalseOrderByNameAsc(Long categoryId);

    List<Product> findByNameContainingIgnoreCaseAndCategoryIdOrderByNameAsc(String name, Long categoryId);
    List<Product> findByNameContainingIgnoreCaseAndCategoryIdAndActiveTrueOrderByNameAsc(String name, Long categoryId);
    List<Product> findByNameContainingIgnoreCaseAndCategoryIdAndActiveFalseOrderByNameAsc(String name, Long categoryId);

    List<Product> findByCategoryId(Long id);

    boolean existsByName(String name);
}
