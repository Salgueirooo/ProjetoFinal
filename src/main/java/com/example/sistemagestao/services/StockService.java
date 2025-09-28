package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Bakery;
import com.example.sistemagestao.domain.Ingredient;
import com.example.sistemagestao.domain.RecipeIngredient;
import com.example.sistemagestao.domain.Stock;
import com.example.sistemagestao.dto.IngredientStockCheckDTO;
import com.example.sistemagestao.dto.RecipeIngredientResponseDTO;
import com.example.sistemagestao.dto.StockResponseDTO;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    @Autowired
    private BakeryRepository bakeryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeIngredientsRepository recipeIngredientsRepository;

    public List<StockResponseDTO> getAll(){
        return stockRepository.findAllByOrderByIngredientNameAscBakeryNameAsc()
                .stream().map(StockResponseDTO::new)
                .toList();
    }

    public List<StockResponseDTO> getAllIngredientStocks(Long ingredientId) {
        return stockRepository.findAllByIngredientIdOrderByBakeryNameAsc(ingredientId)
                .stream().map(StockResponseDTO::new)
                .toList();
    }

    public List<StockResponseDTO> getAllBakeryStocks(Long bakeryId) {
        return stockRepository.findAllByBakeryIdOrderByIngredientNameAsc(bakeryId)
                .stream().map(StockResponseDTO::new)
                .toList();
    }

    public List<StockResponseDTO> searchStockByIngredientName(String ingredientName) {
        return stockRepository.findByIngredientNameContainingIgnoreCaseOrderByIngredientNameAscBakeryNameAsc(ingredientName)
                .stream().map(StockResponseDTO::new)
                .toList();
    }

    public List<StockResponseDTO> searchBakeryStockByIngredientName(Long bakeryId, String ingredientName) {
        return stockRepository.findByIngredientNameContainingIgnoreCaseAndBakeryIdOrderByIngredientNameAsc(ingredientName, bakeryId)
                .stream().map(StockResponseDTO::new)
                .toList();
    }

    @Transactional
    public void initializeStock(Long ingredientId) {
        List<Bakery> allBakeries = bakeryRepository.findAll();
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado."));

        stockRepository.saveAll(allBakeries.stream()
                .map(bakery -> new Stock(ingredient, bakery, 0.0))
                .toList());
    }

    @Transactional
    public void updateStock(Long ingredientId, Long bakeryId, Double quantity) {
        Stock stock = stockRepository.findByIngredientIdAndBakeryId(ingredientId, bakeryId);

        if (stock != null) {
            if (quantity < 0) {
                throw new IllegalArgumentException("A quantidade deve ser um número positivo.");
            }
            stock.setQuantity(quantity);
            stockRepository.save(stock);
        } else {
            throw new EntityNotFoundException("Stock não encontrado para este Ingrediente nesta Pastelaria.");
        }
    }

    public List<IngredientStockCheckDTO> verifyStockForRecipe(Long recipeId, Long bakeryId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new EntityNotFoundException("Receita não encontrada.");
        }
        List<RecipeIngredient> recipeIngredients = recipeIngredientsRepository.findAllByRecipeId(recipeId);
        if (recipeIngredients.isEmpty()) {
            throw new EntityNotFoundException("Ingredientes da receita não encontrados.");
        }

        List<IngredientStockCheckDTO> result = new ArrayList<>();

        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            Stock stock = stockRepository.findByIngredientIdAndBakeryId(
                    recipeIngredient.getIngredient().getId(),
                    bakeryId
            );

            double available = (stock != null) ? stock.getQuantity() : 0.0;
            double required = recipeIngredient.getQuantity();

            result.add(new IngredientStockCheckDTO(
                    new RecipeIngredientResponseDTO(recipeIngredient),
                    available,
                    available >= required
            ));
        }

        return result;
    }

    @Transactional
    public void updateStockAfterUse(Long recipeId, Long bakeryId) {
        List<RecipeIngredient> recipeIngredients = recipeIngredientsRepository.findAllByRecipeId(recipeId);

        if (recipeIngredients.isEmpty()) {
            throw new EntityNotFoundException("Ingredientes da receita não encontrados.");
        }

        for (RecipeIngredient ri : recipeIngredients) {
            Stock stock = stockRepository.findByIngredientIdAndBakeryId(
                    ri.getIngredient().getId(),
                    bakeryId
            );

            double available = (stock != null) ? stock.getQuantity() : 0.0;
            double required  = ri.getQuantity();

            if (available < required) {
                throw new IllegalStateException(
                        "O Ingrediente '" + ri.getIngredient().getName()
                                + "' não tem stock suficiente."
                );
            }
        }

        for (RecipeIngredient ri : recipeIngredients) {
            Stock stock = stockRepository.findByIngredientIdAndBakeryId(
                    ri.getIngredient().getId(),
                    bakeryId
            );
            if (stock == null) {
                throw new EntityNotFoundException(
                        "Stock não encontrado para ingrediente '" + ri.getIngredient().getName() + "'."
                );
            }
            stock.setQuantity(stock.getQuantity() - ri.getQuantity());
            stockRepository.save(stock);
        }
    }
}
