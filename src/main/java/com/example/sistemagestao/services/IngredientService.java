package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.IngredientRequestDTO;
import com.example.sistemagestao.dto.IngredientResponseDTO;
import com.example.sistemagestao.repositories.*;
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
    @Autowired
    private ProducedRecipeIngredientRepository producedRecipeIngredientRepository;
    @Autowired
    private BakeryRepository bakeryRepository;

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

    public List<IngredientResponseDTO> getAllWithLessStockThan(Long bakeryId, Double maxQuantity) {
        Bakery bakery = bakeryRepository.findById(bakeryId)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        return ingredientRepository.findAllWithNoStockInBakery(maxQuantity, bakery)
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
    public void deleteById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado."));

        boolean haveStock = stockRepository.existsByIngredientIdAndQuantityGreaterThan(id, 0.0);

        if (haveStock) {
            throw new IllegalStateException("Ingrediente com stock em pelo menos uma pastelaria.");
        }

        if (recipeIngredientsRepository.existsByIngredientId(ingredient.getId()))
            throw new EntityExistsException("Ingrediente está presente em Receita(s).");

        if (producedRecipeIngredientRepository.existsByIngredientId(ingredient.getId()))
            throw new EntityExistsException("Ingrediente está presente em Receita(s) Produzida(s).");

        List<Stock> stocks = stockRepository.findAllByIngredientId(id);
        if (!stocks.isEmpty())
            stockRepository.deleteAll(stocks);

        ingredientRepository.delete(ingredient);
    }
}
