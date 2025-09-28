package com.example.sistemagestao.controllers;

import com.example.sistemagestao.dto.IngredientStockCheckDTO;
import com.example.sistemagestao.dto.StockResponseDTO;
import com.example.sistemagestao.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/all-ingredient-stocks/{ingredient_id}")
    public List<StockResponseDTO> getAllIngredientStocks(@PathVariable Long ingredient_id){
        return stockService.getAllIngredientStocks(ingredient_id);
    }

    @GetMapping("/search-bakery-stock/{bakery_id}")
    public List<StockResponseDTO> searchBakeryStock(
            @PathVariable Long bakery_id,
            @RequestParam(required = false) String ingredientName
    ) {
        List<StockResponseDTO> result;

        if(ingredientName!=null) {
            result = stockService.searchBakeryStockByIngredientName(bakery_id, ingredientName);
        } else {
            result = stockService.getAllBakeryStocks(bakery_id);
        }

        return result;
    }

    @GetMapping("/search-ingredient-stock")
    public List<StockResponseDTO> searchStock(
            @RequestParam(required = false) String ingredientName
    ) {
        List<StockResponseDTO> result;

        if(ingredientName!=null) {
            result = stockService.searchStockByIngredientName(ingredientName);
        } else {
            result = stockService.getAll();
        }

        return result;
    }

    @PutMapping("/update/{bakery_id}/{ingredient_id}")
    public void updateBakeryStock(
            @PathVariable Long bakery_id,
            @PathVariable Long ingredient_id,
            @RequestBody Double quantity
    ){
        stockService.updateStock(ingredient_id, bakery_id, quantity);
    }

    @GetMapping("/recipe-stock-status/{bakery_id}/{recipe_id}")
    public List<IngredientStockCheckDTO> verifyStockForRecipe(
            @PathVariable Long bakery_id,
            @PathVariable Long recipe_id
    ){
        return stockService.verifyStockForRecipe(recipe_id, bakery_id);
    }
}
