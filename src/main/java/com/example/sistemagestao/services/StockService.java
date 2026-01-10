package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.*;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private RecipeService recipeService;

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
                List.of("/home/" + stock.getBakery().getId() + "/" + NotificationService.FrontendPath.ManageIngredientStock.getPath())
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
                List.of("/home/" + stock.getBakery().getId() + "/" + NotificationService.FrontendPath.ManageIngredientStock.getPath())
        );
    }

    public List<IngredientStockCheckDTO> verifyStockForRecipe(Long recipeId, Long bakeryId, Double dose) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new EntityNotFoundException("Receita não encontrada.");
        }

        if(dose < 0)
            throw new IllegalArgumentException("A dose deve ser maior que 0.");

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
            double required = recipeIngredient.getQuantity() * dose;

            result.add(new IngredientStockCheckDTO(
                    new IngredientResponseDTO(recipeIngredient.getIngredient()),
                    required,
                    available,
                    available >= required
            ));
        }

        return result;
    }

    public boolean isStockSufficientForRecipe(Long recipeId, Long bakeryId, Double dose) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new EntityNotFoundException("Receita não encontrada.");
        }

        if(dose < 0)
            throw new IllegalArgumentException("A dose deve ser maior que 0.");

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
            double required = recipeIngredient.getQuantity() * dose;

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

        if (!isStockSufficientForRecipe(recipeId, bakeryId, dose)) {
            throw new IllegalStateException("Não existe stock suficiente para iniciar esta receita.");
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

    public List<IngredientStockCheckDTO> checkStock(
            Long bakeryId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<STProductQuantityDTO> products =
                orderDetailsRepository.getProductOrderedBetweenDates(
                        bakeryId, startDateTime, endDateTime
                );

        if (products.isEmpty())
            return List.of();

        Map<Long, Long> productQuantities = products.stream()
                .collect(Collectors.toMap(
                        STProductQuantityDTO::productId,
                        STProductQuantityDTO::totalQuantity
                ));

        List<Long> productIds = products.stream()
                .map(STProductQuantityDTO::productId)
                .toList();

        Map<Long, Double> recipeDoses = new HashMap<>();

        List<Recipe> recipes = recipeRepository.findByProduct_IdIn(productIds);

        for (Recipe r : recipes) {
            Long orderedQty = productQuantities.get(r.getProduct().getId());
            if (orderedQty == null || orderedQty == 0)
                continue;

            double doses = (double) orderedQty / r.getNResultingProducts();
            recipeDoses.put(r.getId(), doses);
        }

        List<RecipeIngredient> recipeIngredients = recipeIngredientsRepository.findByRecipeIdIn(recipeDoses.keySet());

        Map<Long, Double> ingredientRequired = new HashMap<>();

        for (RecipeIngredient ri : recipeIngredients) {

            double doses = recipeDoses.getOrDefault(ri.getRecipe().getId(), 0.0);
            if (doses == 0)
                continue;

            double totalIngredient = doses * ri.getQuantity();

            Ingredient ingredient = ingredientRepository.findById(ri.getIngredient().getId())
                    .orElse(null);

            if (ingredient != null && ingredient.getUnits().equals(MeasurentUnits.UNITS)) {
                totalIngredient = Math.ceil(totalIngredient);
            } else {
                totalIngredient = Math.round(totalIngredient * 10.0) / 10.0;
            }

            ingredientRequired.merge(
                    ri.getIngredient().getId(),
                    totalIngredient,
                    Double::sum
            );
        }

        List<Stock> stocks =
                stockRepository.findByBakeryIdAndIngredientIdIn(
                        bakeryId,
                        ingredientRequired.keySet()
                );

        return stocks.stream()
                .map(s -> {
                    Double required = ingredientRequired.getOrDefault(
                            s.getIngredient().getId(), 0.0
                    );

                    Double stock = s.getQuantity();

                    return new IngredientStockCheckDTO(
                            new IngredientResponseDTO(s.getIngredient()),
                            required,
                            stock,
                            stock >= required
                    );
                })
                .toList();
    }
}
