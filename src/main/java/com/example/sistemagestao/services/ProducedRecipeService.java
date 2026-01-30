package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.ActivatedRecipeResponseDTO;
import com.example.sistemagestao.dto.ProducedRecipeRequestDTO;
import com.example.sistemagestao.dto.ProducedRecipeResponseDTO;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ProductStockService productStockService;

    @Transactional
    public void add(ProducedRecipeRequestDTO data, User user) {

        Recipe recipe = recipeRepository.findById(data.recipeId())
                .orElseThrow(() -> new EntityNotFoundException("Receita não encontrada."));

        Bakery bakery = bakeryRepository.findById(data.bakeryId())
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        if (data.dose() <= 0)
            throw new IllegalStateException("O valor da Dose deve ser maior que 0.");

        if(!stockService.isStockSufficientForRecipe(recipe.getId(), bakery.getId(), data.dose())) {
            throw new IllegalStateException("A Pastelaria não tem stock suficiente para produzir esta Receita.");
        }

        ProducedRecipe producedRecipe = new ProducedRecipe(recipe, bakery, data.dose(), user);

        recipe.getIngredientsList().forEach(ri -> {
            ProducedRecipeIngredient pri = new ProducedRecipeIngredient(producedRecipe, ri.getIngredient(), ri.getQuantity() * data.dose());
            producedRecipe.getIngredientsList().add(pri);
        });

        stockService.updateStockAfterUse(recipe.getId(), data.bakeryId(), data.dose());

        producedRecipeRepository.save(producedRecipe);

        notificationService.sendToRole(
                "ROLE_CONFECTIONER",
                producedRecipe.getUser().getName() + " iniciou uma receita de " + producedRecipe.getRecipe().getProduct().getName() + "! Acompanhe na opção ",
                "Receitas Iniciadas.",
                producedRecipe.getBakery(),
                List.of("/home/" + producedRecipe.getBakery().getId() + "/" + NotificationService.FrontendPath.StartedRecipes.getPath(),
                        "/home/" + producedRecipe.getBakery().getId() + "/" +NotificationService.FrontendPath.ManageIngredientStock.getPath())
        );
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

        notificationService.sendToRole(
                "ROLE_CONFECTIONER",
                producedRecipe.getUser().getName() + " cancelou uma receita de " + producedRecipe.getRecipe().getProduct().getName() + ".",
                "",
                producedRecipe.getBakery(),
                List.of("/home/" + producedRecipe.getBakery().getId() + "/" + NotificationService.FrontendPath.StartedRecipes.getPath(),
                        "/home/" + producedRecipe.getBakery().getId() + "/" + NotificationService.FrontendPath.HistoryRecipes.getPath(),
                        "/home/" + producedRecipe.getBakery().getId() + "/" +NotificationService.FrontendPath.ManageIngredientStock.getPath())
        );
    }

    @Transactional
    public void completeProduction(Long id, int quantityDone) {
        ProducedRecipe producedRecipe = producedRecipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receita efetuada não encontrada."));

        if (producedRecipe.getFinalDate()!=null) {
            throw new IllegalStateException("A Receita já foi terminada.");
        }

        producedRecipe.setFinalDate(LocalDateTime.now());
        producedRecipeRepository.save(producedRecipe);

        productStockService.addStock(producedRecipe.getRecipe().getProduct().getId(), producedRecipe.getBakery().getId(), quantityDone);

        notificationService.sendToRole(
                "ROLE_CONFECTIONER",
                "Uma receita de " + producedRecipe.getRecipe().getProduct().getName() + " foi terminada.",
                "",
                producedRecipe.getBakery(),
                List.of("/home/" + producedRecipe.getBakery().getId() + "/" + NotificationService.FrontendPath.StartedRecipes.getPath(),
                        "/home/" + producedRecipe.getBakery().getId() + "/" + NotificationService.FrontendPath.HistoryRecipes.getPath())
        );
    }

    @Transactional
    public void toggleIngredientState(Long id) {
        ProducedRecipeIngredient pri = producedRecipeIngredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente da Receita ativa não encontrada."));

        pri.toggleDone();
        producedRecipeIngredientRepository.save(pri);

        notificationService.sendToRole(
                "ROLE_CONFECTIONER",
                pri.getDone() ? (
                        "Foi adicionado/a " + pri.getIngredient().getName() + " à receita de " + pri.getProducedRecipe().getRecipe().getProduct().getName() + "! Acompanhe a "
                    ) : (
                        "Foi retirado/a " + pri.getIngredient().getName() + " à receita de " + pri.getProducedRecipe().getRecipe().getProduct().getName() + "! Acompanhe a "
                    ),
                "Receita.",
                pri.getProducedRecipe().getBakery(),
                List.of("/home/" + pri.getProducedRecipe().getBakery().getId() + "/" + NotificationService.FrontendPath.StartedRecipes.getPath() + "?recipe=" + pri.getProducedRecipe().getId())
        );
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

    public List<ProducedRecipeResponseDTO> getAllByBakeryAndDate(Long bakeryId, LocalDate date) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(LocalTime.MAX);

        return producedRecipeRepository.findByBakeryIdAndInitialDateBetweenOrderByInitialDateAsc(bakeryId, startDate, endDate)
                .stream()
                .map(ProducedRecipeResponseDTO::new)
                .toList();
    }
}
