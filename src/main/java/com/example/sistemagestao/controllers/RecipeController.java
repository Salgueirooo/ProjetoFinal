package com.example.sistemagestao.controllers;

import com.example.sistemagestao.dto.RecipeIngredientRequestDTO;
import com.example.sistemagestao.dto.RecipeIngredientResponseDTO;
import com.example.sistemagestao.dto.RecipeRequestDTO;
import com.example.sistemagestao.dto.RecipeResponseDTO;
import com.example.sistemagestao.services.RecipeService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @PostMapping("/add")
    public void addRecipe(@RequestBody RecipeRequestDTO data){
        recipeService.add(data);
    }

    @PutMapping("/update/{id}")
    public void updateRecipe(@PathVariable Long id, @RequestBody String preparation){
        recipeService.update(id, preparation);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRecipeById(@PathVariable Long id){
        recipeService.deleteById(id);
    }

    @GetMapping("/{id}")
    public RecipeResponseDTO getRecipeById(@PathVariable Long id){
        return recipeService.getById(id);
    }

    @GetMapping("/search")
    public List<RecipeResponseDTO> searchRecipes(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Boolean active,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<RecipeResponseDTO> result;

        if (isAdmin) {
            if (active != null) {
                if (productName != null)
                    result = recipeService.getAllActiveByName(productName);
                else
                    result = recipeService.getAllActive();
            } else {
                if (productName != null)
                    result = recipeService.getAllByName(productName);
                else
                    result = recipeService.getAll();
            }

        } else {
            if (productName != null)
                result = recipeService.getAllActiveByName(productName);
            else
                result = recipeService.getAllActive();
        }

        return result;
    }

    @GetMapping("/{id}/ingredients")
    public List<RecipeIngredientResponseDTO> getAllRecipeIngredients(@PathVariable Long id){
        return recipeService.getAllRecipeIngredients(id);
    }

    @PostMapping("/add-ingredient")
    public void addIngredient(@RequestBody RecipeIngredientRequestDTO data){
        recipeService.addIngredient(data);
    }

    @PutMapping("/update-ingredient/{id}")
    public void updateIngredient(@PathVariable Long id, @RequestBody Double quantity){
        recipeService.updateIngredient(id, quantity);
    }

    @DeleteMapping("/delete-ingredient/{id}")
    public void deleteIngredient(@PathVariable Long id){
        recipeService.deleteIngredient(id);
    }


}
