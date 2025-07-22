package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByNameAsc();
    List<Product> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
    List<Product> findByCategoryIdOrderByNameAsc(Long categoryId);
    List<Product> findByNameContainingIgnoreCaseAndCategoryIdOrderByNameAsc(String name, Long categoryId);

}
