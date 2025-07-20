package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
