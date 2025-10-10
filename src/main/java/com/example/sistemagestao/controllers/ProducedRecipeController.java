package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.User;
import com.example.sistemagestao.dto.ActivatedRecipeResponseDTO;
import com.example.sistemagestao.dto.ProducedRecipeRequestDTO;
import com.example.sistemagestao.dto.ProducedRecipeResponseDTO;
import com.example.sistemagestao.services.ProducedRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/produced-recipe")
public class ProducedRecipeController {

    @Autowired
    private ProducedRecipeService producedRecipeService;

    @PostMapping("/add")
    public void addProducedRecipe(@AuthenticationPrincipal User user, @RequestBody ProducedRecipeRequestDTO data){
        producedRecipeService.add(data, user);
    }

    @PutMapping("/complete-production/{id}")
    public void completeProduction(@PathVariable Long id) {
        producedRecipeService.completeProduction(id);
    }

    @PutMapping("/toggle-ingredient-state/{id}")
    public void toggleIngredientState(@PathVariable Long id) {
        producedRecipeService.toggleIngredientState(id);
    }

    @GetMapping("/get-active/{id}")
    public ActivatedRecipeResponseDTO getActiveRecipe(@PathVariable Long id) {
        return producedRecipeService.getActiveRecipeById(id);
    }

    @GetMapping("/get-active-recipes/{bakeryId}")
    public List<ProducedRecipeResponseDTO> getActiveRecipes(@PathVariable Long bakeryId) {
        return producedRecipeService.getActiveRecipes(bakeryId);
    }

    @GetMapping("/get-all/{bakeryId}")
    public List<ProducedRecipeResponseDTO> getAllByBakery(@PathVariable Long bakeryId) {
        return producedRecipeService.getAllByBakery(bakeryId);
    }

}
