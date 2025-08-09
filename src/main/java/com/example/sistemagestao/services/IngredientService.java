package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Ingredient;
import com.example.sistemagestao.domain.MeasurentUnits;
import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.domain.RecipeIngredient;
import com.example.sistemagestao.dto.IngredientRequestDTO;
import com.example.sistemagestao.dto.IngredientResponseDTO;
import com.example.sistemagestao.repositories.IngredientRepository;
import com.example.sistemagestao.repositories.RecipeIngredientsRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private RecipeIngredientsRepository recipeIngredientsRepository;

    public List<IngredientResponseDTO> getAll() {
        return ingredientRepository.findAllByOrderByNameAsc()
                .stream().map(IngredientResponseDTO::new)
                .toList();
    }

    public List<IngredientResponseDTO> getAllByName(String name) {
        return ingredientRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name)
                .stream().map(IngredientResponseDTO::new)
                .toList();
    }

    public IngredientResponseDTO getById(Long id) {
        return new IngredientResponseDTO(ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente com ID " + id + " não encontrado")));
    }

    @Transactional
    public ResponseEntity<IngredientResponseDTO> add(IngredientRequestDTO data) {
        MeasurentUnits unit = MeasurentUnits.findByDescription(data.unitDescription());

        Ingredient ingredientData = new Ingredient(data, unit);
        Ingredient saved = ingredientRepository.save(ingredientData);

        return ResponseEntity.ok(new IngredientResponseDTO(saved));
    }

    @Transactional
    public ResponseEntity<IngredientResponseDTO> update(Long id, IngredientRequestDTO newData) {
        MeasurentUnits unit = null;
        if (newData.unitDescription() != null)
            unit = MeasurentUnits.findByDescription(newData.unitDescription());

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente com ID " + id + " não encontrado"));

        ingredient.updateIngredient(newData, unit);
        ingredientRepository.save(ingredient);

        IngredientResponseDTO ingredientResponseDTO = new IngredientResponseDTO(ingredient);
        return ResponseEntity.ok(ingredientResponseDTO);
    }

    @Transactional
    public Long deleteById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingrediente com ID " + id + " não encontrado"));

        List<RecipeIngredient> relatedIngredients = recipeIngredientsRepository.findAllByIngredientId(id);
        recipeIngredientsRepository.deleteAll(relatedIngredients);

        ingredientRepository.delete(ingredient);

        return id;
    }
}
