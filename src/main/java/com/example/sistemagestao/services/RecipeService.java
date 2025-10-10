package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.RecipeIngredientRequestDTO;
import com.example.sistemagestao.dto.RecipeIngredientResponseDTO;
import com.example.sistemagestao.dto.RecipeRequestDTO;
import com.example.sistemagestao.dto.RecipeResponseDTO;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private ProducedRecipeIngredientRepository producedRecipeIngredientRepository;
    @Autowired
    private ProducedRecipeRepository producedRecipeRepository;
    @Autowired
    private ProducedRecipeService producedRecipeService;

    @Transactional
    public void add(RecipeRequestDTO data) {
        Product product = productRepository.findById(data.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        if (recipeRepository.existsByProductId(data.productId())) {
            throw new EntityExistsException("Já existe uma Receita para este Produto.");
        }

        Recipe recipe = new Recipe(product, data.preparation());
        recipeRepository.save(recipe);
    }

    @Transactional
    public void update(Long id, String preparation){
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada."));

        recipe.setPreparation(preparation);
        recipeRepository.save(recipe);
    }

    @Transactional
    public void deleteById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada."));

        List<ProducedRecipe> producedRecipes = producedRecipeRepository.findAllByRecipeId(id);
        for (ProducedRecipe pr : producedRecipes) {
            producedRecipeService.delete(pr.getId());
        }

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

        List<ProducedRecipeIngredient> producedRecipeIngredients = producedRecipeIngredientRepository
                .findAllByProducedRecipe_Recipe_IdAndRecipeIngredient_Ingredient_Id(
                        recipeIngredient.getRecipe().getId(),
                        recipeIngredient.getIngredient().getId()
                );

        producedRecipeIngredientRepository.deleteAll(producedRecipeIngredients);

        recipeIngredientsRepository.delete(recipeIngredient);
    }
}
