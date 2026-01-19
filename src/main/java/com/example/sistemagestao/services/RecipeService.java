package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.*;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private RecipeIngredientsRepository recipeIngredientsRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private OrderDetailsRepository  orderDetailsRepository;
    @Autowired
    private ProducedRecipeRepository producedRecipeRepository;

    @Transactional
    public void add(RecipeRequestDTO data) {
        Product product = productRepository.findById(data.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        if (recipeRepository.existsByProductId(data.productId())) {
            throw new EntityExistsException("Já existe uma Receita para este Produto.");
        }

        if (data.nResultingProducts() <= 0)
            throw new IllegalArgumentException("O número de produtos resultantes deve ser maior que 0.");

        Recipe recipe = new Recipe(product, data.preparation(), data.nResultingProducts());
        recipeRepository.save(recipe);
    }

    @Transactional
    public void update(Long id, RecipeRequestDTO newData) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada."));

        if(newData.nResultingProducts() != null && newData.nResultingProducts() < 0) {
            throw new IllegalArgumentException("O número de produtos resultantes deve ser maior que 0.");
        }

        recipe.upgradeRecipe(newData);
        recipeRepository.save(recipe);
    }

    @Transactional
    public void deleteById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada."));

        List<RecipeIngredient> relatedIngredients = recipeIngredientsRepository.findAllByRecipeId(id);
        for (RecipeIngredient ri : relatedIngredients) {
            deleteIngredient(ri.getId());
        }

        recipeRepository.delete(recipe);
    }

    public RecipeResponseDTO getById(Long id) {
        return new RecipeResponseDTO(recipeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada.")));
    }

    public List<RecipeResponseDTO> getAll() {
        return recipeRepository.findAllByOrderByProductNameAsc()
                .stream()
                .map(RecipeResponseDTO::new)
                .toList();
    }

    public List<RecipeResponseDTO> getAllByName(String name) {
        return recipeRepository.findByProductNameContainingIgnoreCaseOrderByProductNameAsc(name)
                .stream()
                .map(RecipeResponseDTO::new)
                .toList();
    }

    public List<RecipeResponseDTO> getAllActive() {
        return recipeRepository.findByProduct_ActiveTrueOrderByProductNameAsc()
                .stream()
                .map(RecipeResponseDTO::new)
                .toList();
    }

    public List<RecipeResponseDTO> getAllActiveByName(String name) {
        return recipeRepository.findByProduct_ActiveTrueAndProductNameContainingIgnoreCaseOrderByProductNameAsc(name)
                .stream()
                .map(RecipeResponseDTO::new)
                .toList();
    }

    public List<RecipeIngredientResponseDTO> getAllRecipeIngredients (Long id) {
        if(!recipeRepository.existsById(id))
            throw new EntityNotFoundException("Receita não encontrada.");

        return recipeIngredientsRepository.findAllByRecipeId(id)
                .stream()
                .map(RecipeIngredientResponseDTO::new)
                .toList();
    }

    @Transactional
    public void addIngredient(RecipeIngredientRequestDTO data) {
        Recipe recipe = recipeRepository.findById(data.recipeId())
                .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada."));

        Ingredient ingredient = ingredientRepository.findById(data.ingredientId())
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado."));

        if (recipeIngredientsRepository.existsByRecipeIdAndIngredientId(data.recipeId(), data.ingredientId())) {
            throw new EntityExistsException("A Receita já possui esse Ingrediente.");
        }

        if (data.quantity() < 0) {
            throw new IllegalArgumentException("A quantidade deve ser um número positivo.");
        }

        RecipeIngredient recipeIngredient = new RecipeIngredient(recipe, ingredient, data.quantity());
        recipeIngredientsRepository.save(recipeIngredient);
    }

    @Transactional
    public void updateIngredient(Long recipeIngredientId, Double quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("A quantidade deve ser um número positivo.");
        }

        RecipeIngredient recipeIngredient = recipeIngredientsRepository.findById(recipeIngredientId)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente da receita não encontrado."));

        recipeIngredient.setQuantity(quantity);
        recipeIngredientsRepository.save(recipeIngredient);
    }

    @Transactional
    public void deleteIngredient(Long id) {
        RecipeIngredient recipeIngredient = recipeIngredientsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente da receita não encontrado."));

        recipeIngredientsRepository.delete(recipeIngredient);
    }

    public List<RecipeProductionTaskDTO> getProductionTasks(
            Long bakeryId,
            LocalDate date
    ) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<STProductQuantityDTO> products =
                orderDetailsRepository.getProductOrderedBetweenDates(
                        bakeryId, start, end
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

        List<Recipe> recipesByProductId =
                recipeRepository.findByProduct_IdIn(productIds);

        Map<Long, Double> recipeDoses = new HashMap<>();
        Map<Long, Long> recipeTotalProducts = new HashMap<>();

        for (Recipe r : recipesByProductId) {

            Long orderedQty = productQuantities.get(r.getProduct().getId());
            if (orderedQty == null || orderedQty == 0)
                continue;

            double doses = (double) orderedQty / r.getNResultingProducts();

            recipeDoses.merge(r.getId(), doses, Double::sum);
            recipeTotalProducts.merge(r.getId(), orderedQty, Long::sum);
        }

        if (recipeDoses.isEmpty())
            return List.of();

        List<ProducedRecipe> producedRecipes =
                producedRecipeRepository.findByBakeryIdAndInitialDateBetween(
                        bakeryId, start, end
                );

        Map<Long, Double> producedDosesByRecipe = producedRecipes.stream()
                .collect(Collectors.groupingBy(
                        pr -> pr.getRecipe().getId(),
                        Collectors.summingDouble(ProducedRecipe::getDose)
                ));

        List<Recipe> recipes =
                recipeRepository.findByIdInOrderByProduct_NameAsc(recipeDoses.keySet());

        return recipes.stream()
                .map(r -> new RecipeProductionTaskDTO(
                        r.getId(),
                        r.getProduct().getName(),
                        recipeDoses.get(r.getId()),
                        recipeTotalProducts.getOrDefault(r.getId(), 0L),
                        producedDosesByRecipe.getOrDefault(r.getId(), 0.0)
                ))
                .toList();
    }
}
