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

    @RequestMapping("/search")
    public ResponseEntity<List<IngredientResponseDTO>> searchIngredients(
            @RequestParam(required = false) String name
    ) {
        List<IngredientResponseDTO> result;

        if (name != null)
            result = ingredientService.getAllByName(name);
        else
            result = ingredientService.getAll();

        return ResponseEntity.ok(result);
    }

    @RequestMapping("/{id}")
    public IngredientResponseDTO getIngredientById(@PathVariable Long id){
        return ingredientService.getById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<IngredientResponseDTO> addIngredient(@RequestBody IngredientRequestDTO data){
        return ingredientService.add(data);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<IngredientResponseDTO> updateIngredient(@PathVariable Long id, @RequestBody IngredientRequestDTO newData){
        return ingredientService.update(id, newData);
    }

    @DeleteMapping("/delete/{id}")
    public Long deleteIngredientById(@PathVariable Long id){
        return ingredientService.deleteById(id);
    }
}
