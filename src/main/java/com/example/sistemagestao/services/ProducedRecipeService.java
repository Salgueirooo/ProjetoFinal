package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.ActivatedRecipeResponseDTO;
import com.example.sistemagestao.dto.ProducedRecipeRequestDTO;
import com.example.sistemagestao.dto.ProducedRecipeResponseDTO;
import com.example.sistemagestao.repositories.*;
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
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void add(ProducedRecipeRequestDTO data, User user) {
        Product product = productRepository.findById(data.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        Recipe recipe = recipeRepository.findByProduct_id(data.productId())
                .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada."));

        Bakery bakery = bakeryRepository.findById(data.bakeryId())
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        if (data.dose() <= 0)
            throw new IllegalStateException("O valor da Dose deve ser maior que 0.");

        if(!stockService.isStockSufficientForRecipe(recipe.getId(), bakery.getId())) {
            throw new IllegalStateException("A Pastelaria não tem stock suficiente para produzir esta Receita.");
        }

        ProducedRecipe producedRecipe = new ProducedRecipe(product, bakery, recipe.getPreparation(), user);

        recipe.getIngredientsList().forEach(ri -> {
            ProducedRecipeIngredient pri = new ProducedRecipeIngredient(producedRecipe, ri.getIngredient(), ri.getQuantity() * data.dose());
            producedRecipe.getIngredientsList().add(pri);
        });

        stockService.updateStockAfterUse(recipe.getId(), data.bakeryId(), data.dose());

        producedRecipeRepository.save(producedRecipe);
    }

    @Transactional
    public void cancelRecipe(Long id){
        ProducedRecipe producedRecipe = producedRecipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita efetuada não encontrada."));

        if(producedRecipe.getFinalDate() != null)
            throw new IllegalStateException("Impossível apagar uma receita terminada.");

        stockService.updateStockAfterRecipeCancelled(producedRecipe.getId());

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
        ProducedRecipe producedRecipe = producedRecipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita iniciada não encontrada."));

        if(producedRecipe.getFinalDate()!=null)
            throw new IllegalStateException("Receita terminada.");

        return new ActivatedRecipeResponseDTO(producedRecipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita iniciada não encontrada.")));
    }

    public List<ProducedRecipeResponseDTO> getActiveRecipes(Long bakeryId) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        return producedRecipeRepository.findByBakeryIdAndFinalDateIsNullOrderByInitialDateAsc(bakeryId)
                .stream()
                .map(ProducedRecipeResponseDTO::new)
                .toList();
    }

    public List<ProducedRecipeResponseDTO> getAllByBakery(Long bakeryId) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        return producedRecipeRepository.findByBakeryIdOrderByInitialDateDesc(bakeryId)
                .stream()
                .map(ProducedRecipeResponseDTO::new)
                .toList();
    }
}
