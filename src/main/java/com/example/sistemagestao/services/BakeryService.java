package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.BakeryRequestDTO;
import com.example.sistemagestao.dto.BakeryResponseDTO;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BakeryService {

    @Autowired
    private BakeryRepository bakeryRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ProducedRecipeRepository producedRecipeRepository;

    @Transactional
    public void add(BakeryRequestDTO data) {
        if (bakeryRepository.existsByName(data.name())) {
            throw new EntityExistsException("Já existe uma Pastelaria com esse nome.");
        }

        Bakery bakery = new Bakery(data);
        bakeryRepository.save(bakery);

        List<Ingredient> ingredients = ingredientRepository.findAll();
        if (ingredients.isEmpty()) return;

        List<Stock> stocks = ingredients
                .stream()
                .map(ingredient -> new Stock(ingredient, bakery, 0.0))
                .toList();

        stockRepository.saveAll(stocks);
    }

    @Transactional
    public void update(Long id, BakeryRequestDTO newData) {
        Bakery bakery = bakeryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        if (bakeryRepository.existsByName(newData.name())) {
            throw new EntityExistsException("Já existe uma Pastelaria com esse nome.");
        }

        bakery.updateBakery(newData);
        bakeryRepository.save(bakery);
    }

    // Fica a faltar eliminar as encomendas referentes a esta pastelaria
    @Transactional
    public void delete(Long id) {
        Bakery bakery = bakeryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        if(stockRepository.existsByBakeryIdAndQuantityGreaterThan(id, 0.0))
            throw new IllegalStateException("Existe stock de ingredientes nesta pastelaria.");

        List<ProducedRecipe> producedRecipes = producedRecipeRepository.findByBakeryId(id);

        if (!producedRecipes.isEmpty()) {
            producedRecipeRepository.deleteAll(producedRecipes);
        }

        stockRepository.deleteByBakeryId(id);

        bakeryRepository.delete(bakery);
    }

    public List<BakeryResponseDTO> getAll() {
        return bakeryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(BakeryResponseDTO::new)
                .toList();
    }
}
