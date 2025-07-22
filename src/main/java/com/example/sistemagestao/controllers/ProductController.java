package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.dto.ProductRequestDTO;
import com.example.sistemagestao.dto.ProductResponseDTO;
import com.example.sistemagestao.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public void addProduct(@RequestBody ProductRequestDTO data) {
        productService.addProduct(data);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO newData) {
        return productService.updateProduct(id, newData);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProductById(@PathVariable Long id) {
        productService.deleteById(id);
    }

    @GetMapping("/all")
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId
    ) {
        List<ProductResponseDTO> result;

        if (name != null && categoryId != null) {
            result = productService.getAllProductsByNameAndCategory(name, categoryId);
        } else if (name != null) {
            result = productService.getAllProductsByName(name);
        } else if (categoryId != null) {
            result = productService.getAllProductsByCategory(categoryId);
        } else {
            result = productService.getAllProducts();
        }

        return ResponseEntity.ok(result);
    }
}
