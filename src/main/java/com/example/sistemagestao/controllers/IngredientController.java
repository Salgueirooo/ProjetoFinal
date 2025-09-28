package com.example.sistemagestao.controllers;

import com.example.sistemagestao.dto.IngredientRequestDTO;
import com.example.sistemagestao.dto.IngredientResponseDTO;
import com.example.sistemagestao.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/ingredient")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping("/search")
    public List<IngredientResponseDTO> searchIngredients(
            @RequestParam(required = false) String name
    ) {
        List<IngredientResponseDTO> result;

        if (name != null)
            result = ingredientService.getAllByName(name);
        else
            result = ingredientService.getAll();

        return result;
    }

    @GetMapping("/without-stock")
    public List<IngredientResponseDTO> getAllIngredientsWithoutStock(){
        return ingredientService.getAllWithoutStock();
    }

    @GetMapping("/{id}")
    public IngredientResponseDTO getIngredientById(@PathVariable Long id){
        return ingredientService.getById(id);
    }

    @PostMapping("/add")
    public void addIngredient(@RequestBody IngredientRequestDTO data){
        ingredientService.add(data);
    }

    @PutMapping("/update/{id}")
    public void updateIngredient(@PathVariable Long id, @RequestBody IngredientRequestDTO newData){
        ingredientService.update(id, newData);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteIngredientById(@PathVariable Long id){
        ingredientService.deleteById(id);
    }
}
