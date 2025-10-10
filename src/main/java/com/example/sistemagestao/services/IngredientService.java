package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.IngredientRequestDTO;
import com.example.sistemagestao.dto.IngredientResponseDTO;
import com.example.sistemagestao.repositories.IngredientRepository;
import com.example.sistemagestao.repositories.RecipeIngredientsRepository;
import com.example.sistemagestao.repositories.RecipeRepository;
import com.example.sistemagestao.repositories.StockRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeIngredientsRepository recipeIngredientsRepository;
    @Autowired
    private RecipeService recipeService;

    public List<IngredientResponseDTO> getAll() {
        return ingredientRepository.findAllByOrderByNameAsc()
                .stream().map(IngredientResponseDTO::new)
                .toList();
    }

    public List<IngredientResponseDTO> getAllByName(String name) {
        return ingredientRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name)
                .stream().map(IngredientResponseDTO::new)
                .toList();
    }

    public IngredientResponseDTO getById(Long id) {
        return new IngredientResponseDTO(ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado.")));
    }

    public List<IngredientResponseDTO> getAllWithoutStock() {
        return ingredientRepository.findAllWithNoStockInAnyBakery()
                .stream().map(IngredientResponseDTO::new)
                .toList();
    }

    @Transactional
    public void add(IngredientRequestDTO data) {
        MeasurentUnits unit = MeasurentUnits.findByDescription(data.unitDescription());

        if (ingredientRepository.existsByName(data.name())) {
            throw new EntityExistsException("Já existe um Ingrediente com esse nome.");
        }

        Ingredient ingredientData = new Ingredient(data, unit);
        ingredientRepository.save(ingredientData);

        stockService.initializeStock(ingredientData.getId());
    }

    @Transactional
    public void update(Long id, IngredientRequestDTO newData) {
        MeasurentUnits unit = null;
        if (newData.unitDescription() != null)
            unit = MeasurentUnits.findByDescription(newData.unitDescription());

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado."));

        ingredient.updateIngredient(newData, unit);
        ingredientRepository.save(ingredient);
    }

    @Transactional
    public void deleteById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado."));

        boolean haveStock = stockRepository.existsByIngredientIdAndQuantityGreaterThan(id, 0.0);

        if (haveStock) {
            throw new IllegalStateException("Ingrediente com stock em pelo menos uma pastelaria.");
        }

        if(recipeRepository.existsByProductId(id))
            throw new EntityExistsException("Ainda existe uma Receita para este Produto.");

        List<Stock> stocks = stockRepository.findAllByIngredientId(id);
        if (!stocks.isEmpty())
            stockRepository.deleteAll(stocks);

        List<RecipeIngredient> recipeIngredients = recipeIngredientsRepository.findAllByIngredientId(id);
        if (!recipeIngredients.isEmpty()) {
            for (RecipeIngredient ri : recipeIngredients) {
                recipeService.deleteIngredient(ri.getId());
            }
        }

        ingredientRepository.delete(ingredient);
    }
}
