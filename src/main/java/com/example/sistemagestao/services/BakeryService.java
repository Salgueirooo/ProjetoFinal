package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Bakery;
import com.example.sistemagestao.dto.BakeryRequestDTO;
import com.example.sistemagestao.dto.BakeryResponseDTO;
import com.example.sistemagestao.repositories.BakeryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BakeryService {

    @Autowired
    private BakeryRepository bakeryRepository;

    @Transactional
    public void add(BakeryRequestDTO data) {
        Bakery bakery = new Bakery(data);
        bakeryRepository.save(bakery);
    }

    @Transactional
    public void update(Long id, BakeryRequestDTO newData) {
        Bakery bakery = bakeryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada"));

        bakery.updateBakery(newData);
        bakeryRepository.save(bakery);
    }

    //Fica a faltar eliminar as encomendas referentes a esta pastelaria
    // as receitas produzidas na mesma
    // e o stock
    @Transactional
    public void delete(Long id) {
        Bakery bakery = bakeryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada"));

        bakeryRepository.delete(bakery);
    }
}
