package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
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
    @Autowired
    private ProducedRecipeRepository producedRecipeRepository;
    @Autowired
    private ProducedRecipeIngredientRepository producedRecipeIngredientRepository;
    @Autowired
    private NotificationService notificationService;

    public List<StockResponseDTO> getAll(){
        return stockRepository.findAllByOrderByIngredientNameAscBakeryNameAsc()
                .stream().map(StockResponseDTO::new)
                .toList();
    }

    public List<StockResponseDTO> getAllIngredientStocks(Long ingredientId) {
        if(!ingredientRepository.existsById(ingredientId))
            throw new EntityNotFoundException("Ingrediente não encontrado.");

        return stockRepository.findAllByIngredientIdOrderByBakeryNameAsc(ingredientId)
                .stream().map(StockResponseDTO::new)
                .toList();
    }

    public List<StockResponseDTO> getAllBakeryStocks(Long bakeryId) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

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
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

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

        notificationService.sendToRole(
                "ROLE_CONFECTIONER",
                "O stock do ingrediente " + stock.getIngredient().getName() + " foi atualizado!\nInformações disponíveis ",
                "aqui.",
                stock.getBakery(),
                List.of("/home/" + stock.getBakery().getId() + "/" + NotificationService.FrontendPath.ManageStock.getPath())
        );
    }

    @Transactional
    public void addStock(Long ingredientId, Long bakeryId, Double quantity) {
        Stock stock = stockRepository.findByIngredientIdAndBakeryId(ingredientId, bakeryId);

        if (stock != null) {
            if (quantity < 0) {
                throw new IllegalArgumentException("A valor a acrescentar deve ser um número positivo.");
            }
            stock.setQuantity(stock.getQuantity() + quantity);
            stockRepository.save(stock);
        } else {
            throw new EntityNotFoundException("Stock não encontrado para este Ingrediente nesta Pastelaria.");
        }

        notificationService.sendToRole(
                "ROLE_CONFECTIONER",
                "O stock do ingrediente " + stock.getIngredient().getName() + " foi atualizado!\nInformações disponíveis ",
                "aqui.",
                stock.getBakery(),
                List.of("/home/" + stock.getBakery().getId() + "/" + NotificationService.FrontendPath.ManageStock.getPath())
        );
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

    public boolean isStockSufficientForRecipe(Long recipeId, Long bakeryId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new EntityNotFoundException("Receita não encontrada.");
        }
        List<RecipeIngredient> recipeIngredients = recipeIngredientsRepository.findAllByRecipeId(recipeId);
        if (recipeIngredients.isEmpty()) {
            throw new EntityNotFoundException("Ingredientes da receita não encontrados.");
        }

        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            Stock stock = stockRepository.findByIngredientIdAndBakeryId(
                    recipeIngredient.getIngredient().getId(),
                    bakeryId
            );

            double available = (stock != null) ? stock.getQuantity() : 0.0;
            double required = recipeIngredient.getQuantity();

            if (available < required) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public void updateStockAfterUse(Long recipeId, Long bakeryId, Double dose) {
        if (!recipeRepository.existsById(recipeId))
            throw new EntityNotFoundException("Receita não encontrada.");

        if (!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

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
            double required  = ri.getQuantity() * dose;

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
            stock.setQuantity(stock.getQuantity() - (ri.getQuantity() * dose));
            stockRepository.save(stock);
        }
    }

    @Transactional
    public void updateStockAfterRecipeCancelled(Long producedRecipeId) {
        ProducedRecipe producedRecipe = producedRecipeRepository.findById(producedRecipeId)
                .orElseThrow(() -> new EntityNotFoundException("Receita produzida não encontrada."));

        List<ProducedRecipeIngredient> recipeIngredients = producedRecipeIngredientRepository.findAllByProducedRecipe_Id(producedRecipeId);

        if (recipeIngredients.isEmpty()) {
            throw new EntityNotFoundException("Ingredientes da receita produzida não encontrados.");
        }

        for (ProducedRecipeIngredient pri : recipeIngredients) {
            Stock stock = stockRepository.findByIngredientIdAndBakeryId(
                    pri.getIngredient().getId(),
                    producedRecipe.getBakery().getId()
            );
            if (stock == null) {
                throw new EntityNotFoundException(
                        "Stock não encontrado para ingrediente '" + pri.getIngredient().getName() + "'."
                );
            }
            stock.setQuantity(stock.getQuantity() + pri.getQuantity());
            stockRepository.save(stock);
        }
    }
}
