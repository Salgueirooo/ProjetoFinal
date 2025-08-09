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
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody ProductRequestDTO data) {
        return productService.add(data);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO newData) {
        return productService.update(id, newData);
    }

    @PutMapping("/change-state/{id}")
    public ResponseEntity<ProductResponseDTO> changeProductStateById(@PathVariable Long id) {
        return productService.changeStateById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active
    ) {
        List<ProductResponseDTO> result;

        if (active != null) {
            if (name != null && categoryId != null) {
                result = active ? productService.getAllActiveByNameAndCategory(name, categoryId)
                        : productService.getAllInactiveByNameAndCategory(name, categoryId);
            } else if (name != null) {
                result = active ? productService.getAllActiveByName(name)
                        : productService.getAllInactiveByName(name);
            } else if (categoryId != null) {
                result = active ? productService.getAllActiveByCategory(categoryId)
                        : productService.getAllInactiveByCategory(categoryId);
            } else {
                result = active ? productService.getAllActive()
                        : productService.getAllInactive();
            }
        } else {
            if (name != null && categoryId != null) {
                result = productService.getAllByNameAndCategory(name, categoryId);
            } else if (name != null) {
                result = productService.getAllByName(name);
            } else if (categoryId != null) {
                result = productService.getAllByCategory(categoryId);
            } else {
                result = productService.getAll();
            }
        }

        return ResponseEntity.ok(result);
    }
}
