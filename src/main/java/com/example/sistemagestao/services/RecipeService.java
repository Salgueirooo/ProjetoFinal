package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Ingredient;
import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.domain.Recipe;
import com.example.sistemagestao.domain.RecipeIngredient;
import com.example.sistemagestao.dto.RecipeIngredientRequestDTO;
import com.example.sistemagestao.dto.RecipeIngredientResponseDTO;
import com.example.sistemagestao.dto.RecipeRequestDTO;
import com.example.sistemagestao.dto.RecipeResponseDTO;
import com.example.sistemagestao.repositories.IngredientRepository;
import com.example.sistemagestao.repositories.ProductRepository;
import com.example.sistemagestao.repositories.RecipeIngredientsRepository;
import com.example.sistemagestao.repositories.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Transactional
    public void add(RecipeRequestDTO data) {
        Product product = productRepository.findById(data.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        Recipe recipe = new Recipe(product, data.description());
        recipeRepository.save(recipe);
    }

    @Transactional
    public void update(Long id, String preparation){
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita não existe"));

        recipe.setPreparation(preparation);
        recipeRepository.save(recipe);
    }

    @Transactional
    public void deleteById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada"));

        List<RecipeIngredient> relatedIngredients = recipeIngredientsRepository.findAllByRecipeId(id);
        recipeIngredientsRepository.deleteAll(relatedIngredients);

        recipeRepository.delete(recipe);
    }

    public RecipeResponseDTO getById(Long id) {
        return new RecipeResponseDTO(recipeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Receita com ID " + id + " não encontrada")));
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

    public List<RecipeIngredientResponseDTO> getAllRecipeIngredients (Long id) {
        return recipeIngredientsRepository.findAllByRecipeId(id)
                .stream()
                .map(RecipeIngredientResponseDTO::new)
                .toList();
    }

    @Transactional
    public void addIngredient(RecipeIngredientRequestDTO data) {
        Recipe recipe = recipeRepository.findById(data.recipeId())
                .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada"));

        Ingredient ingredient = ingredientRepository.findById(data.ingredientId())
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente não encontrado"));

        RecipeIngredient recipeIngredient = new RecipeIngredient(recipe, ingredient, data.quantity());
        recipeIngredientsRepository.save(recipeIngredient);
    }

    @Transactional
    public void updateIngredient(Long recipeIngredientId, Double quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("A quantidade deve ser um valor numérico positivo");
        }

        RecipeIngredient recipeIngredient = recipeIngredientsRepository.findById(recipeIngredientId)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente da receita não encontrado"));

        recipeIngredient.setQuantity(quantity);
        recipeIngredientsRepository.save(recipeIngredient);
    }

    @Transactional
    public void deleteIngredient(Long id) {
        RecipeIngredient recipeIngredient = recipeIngredientsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente da receita não encontrado"));

        recipeIngredientsRepository.delete(recipeIngredient);
    }
}
