package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.dto.ProductRequestDTO;
import com.example.sistemagestao.dto.ProductResponseDTO;
import com.example.sistemagestao.repositories.ProductRepository;
import com.example.sistemagestao.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public void addProduct(@RequestBody ProductRequestDTO data) {
        productService.addProduct(data);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getById(@PathVariable Long id){
        return productService.getById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO newData) {
        return productService.updateProduct(id, newData);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Long id){
        productRepository.deleteById(id);
    }

    @GetMapping("/all")
    public List<ProductResponseDTO> getAll(){
        return productService.getAll();
    }
}
