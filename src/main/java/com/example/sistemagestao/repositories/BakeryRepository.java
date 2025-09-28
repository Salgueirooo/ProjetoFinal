package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.Bakery;
import com.example.sistemagestao.dto.BakeryResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BakeryRepository extends JpaRepository<Bakery, Long> {
    List<Bakery> findAllByOrderByNameAsc();
    boolean existsByName(String name);
}
