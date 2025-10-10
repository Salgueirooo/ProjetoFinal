package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.ActivatedRecipeResponseDTO;
import com.example.sistemagestao.dto.ProducedRecipeRequestDTO;
import com.example.sistemagestao.dto.ProducedRecipeResponseDTO;
import com.example.sistemagestao.repositories.BakeryRepository;
import com.example.sistemagestao.repositories.ProducedRecipeIngredientRepository;
import com.example.sistemagestao.repositories.ProducedRecipeRepository;
import com.example.sistemagestao.repositories.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducedRecipeService {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private BakeryRepository bakeryRepository;
    @Autowired
    private ProducedRecipeRepository producedRecipeRepository;
    @Autowired
    private ProducedRecipeIngredientRepository producedRecipeIngredientRepository;
    @Autowired
    private StockService stockService;

    @Transactional
    public void add(ProducedRecipeRequestDTO data, User user) {
        Recipe recipe = recipeRepository.findById(data.recipeId())
                .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada."));

        Bakery bakery = bakeryRepository.findById(data.bakeryId())
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        ProducedRecipe producedRecipe = new ProducedRecipe(recipe, bakery, user);

        recipe.getIngredientsList().forEach(ri -> {
            ProducedRecipeIngredient pri = new ProducedRecipeIngredient(producedRecipe, ri);
            producedRecipe.getIngredientsList().add(pri);
        });

        stockService.updateStockAfterUse(data.recipeId(), data.bakeryId());

        producedRecipeRepository.save(producedRecipe);
    }

    @Transactional
    public void delete(Long id){
        ProducedRecipe producedRecipe = producedRecipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita efetuada não encontrada."));

        List<ProducedRecipeIngredient> producedRecipeIngredients = producedRecipeIngredientRepository.findAllByProducedRecipe_Id(id);
        if (!producedRecipeIngredients.isEmpty()) {
            producedRecipeIngredientRepository.deleteAll(producedRecipeIngredients);
        }

        producedRecipeRepository.delete(producedRecipe);
    }

    @Transactional
    public void completeProduction(Long id) {
        ProducedRecipe producedRecipe = producedRecipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita efetuada não encontrada."));

        if (producedRecipe.getFinalDate()!=null) {
            throw new IllegalStateException("A Receita já foi terminada.");
        }

        producedRecipe.setFinalDate(LocalDateTime.now());
        producedRecipeRepository.save(producedRecipe);
    }

    @Transactional
    public void toggleIngredientState(Long id) {
        ProducedRecipeIngredient pri = producedRecipeIngredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente da Receita ativa não encontrada."));

        pri.toggleDone();
    }

    public ActivatedRecipeResponseDTO getActiveRecipeById(Long id) {
        return new ActivatedRecipeResponseDTO(producedRecipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita iniciada não encontrada.")));
    }

    public List<ProducedRecipeResponseDTO> getActiveRecipes(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        return producedRecipeRepository.findByBakeryIdAndFinalDateIsNullOrderByInitialDateAsc(bakeryId)
                .stream()
                .map(ProducedRecipeResponseDTO::new)
                .toList();
    }

    public List<ProducedRecipeResponseDTO> getAllByBakery(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        return producedRecipeRepository.findByBakeryIdOrderByInitialDateDesc(bakeryId)
                .stream()
                .map(ProducedRecipeResponseDTO::new)
                .toList();
    }
}
