package com.example.sistemagestao.controllers;

import com.example.sistemagestao.dto.IngredientResponseDTO;
import com.example.sistemagestao.dto.ProductStockCheckDTO;
import com.example.sistemagestao.dto.ProductStockResponseDTO;
import com.example.sistemagestao.services.IngredientService;
import com.example.sistemagestao.services.ProductStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product-stock")
public class ProductStockController {
    @Autowired
    private ProductStockService productStockService;

    @PutMapping("/update/{bakeryId}/{productId}")
    public void updateProductStock(@PathVariable Long bakeryId, @PathVariable Long productId, @RequestBody int newQuantity) {
        productStockService.update(productId, bakeryId, newQuantity);
    }

    @PutMapping("/add-stock/{bakeryId}/{productId}")
    public void addProductStock(@PathVariable Long bakeryId, @PathVariable Long productId,  @RequestBody int quantity) {
        productStockService.addStock(productId, bakeryId, quantity);
    }

    @GetMapping("/get-all-by-bakery/{bakeryId}")
    public List<ProductStockResponseDTO> getProductStock(@PathVariable Long bakeryId) {
        return productStockService.getAllByBakery(bakeryId);
    }

    @GetMapping("/verify-stock-order/{id}")
    public List<ProductStockCheckDTO> verifyStockOrder(@PathVariable Long id) {
        return productStockService.verifyStockForOrder(id);
    }
}
