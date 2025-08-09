package com.example.sistemagestao.controllers;

import com.example.sistemagestao.dto.RecipeIngredientRequestDTO;
import com.example.sistemagestao.dto.RecipeIngredientResponseDTO;
import com.example.sistemagestao.dto.RecipeRequestDTO;
import com.example.sistemagestao.dto.RecipeResponseDTO;
import com.example.sistemagestao.services.RecipeService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @PostMapping("/add")
    public ResponseEntity<RecipeResponseDTO> addRecipe(@RequestBody RecipeRequestDTO data){
        return recipeService.add(data);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RecipeResponseDTO> updateRecipe(@PathVariable Long id, @RequestBody String preparation){
        return recipeService.update(id, preparation);
    }

    @DeleteMapping("/delete/{id}")
    public Long deleteRecipeById(@PathVariable Long id){
        return recipeService.deleteById(id);
    }

    @GetMapping("/{id}")
    public RecipeResponseDTO getRecipeById(@PathVariable Long id){
        return recipeService.getById(id);
    }

    @GetMapping("/all")
    public List<RecipeResponseDTO> getAll(){
        return recipeService.getAll();
    }

    @GetMapping("/search")
    public List<RecipeResponseDTO> searchRecipes(
            @RequestParam(required = false) String name
    ) {
        List<RecipeResponseDTO> result;

        if (name != null)
            result = recipeService.getAllByName(name);
        else
            result = recipeService.getAll();

        return result;
    }

    @GetMapping("/{id}/ingredients")
    public List<RecipeIngredientResponseDTO> getAllRecipeIngredients(@PathVariable Long id){
        return recipeService.getAllRecipeIngredients(id);
    }

    @PostMapping("/add-ingredient")
    public ResponseEntity<RecipeIngredientResponseDTO> addIngredient(@RequestBody RecipeIngredientRequestDTO data){
        return recipeService.addIngredient(data);
    }

    @PutMapping("/update-ingredient/{id}")
    public ResponseEntity<RecipeIngredientResponseDTO> updateIngredient(@PathVariable Long id, @RequestBody Double quantity){
        return recipeService.updateIngredient(id, quantity);
    }

    @DeleteMapping("/delete-ingredient/{id}")
    public Long deleteIngredient(@PathVariable Long id){
        return recipeService.deleteIngredient(id);
    }


}
