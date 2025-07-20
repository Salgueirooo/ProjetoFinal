package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.dto.ProductRequestDTO;
import com.example.sistemagestao.dto.ProductResponseDTO;
import com.example.sistemagestao.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @PostMapping
    public void addProduct(@RequestBody ProductRequestDTO data) {
        Product productData = new Product(data);
        repository.save(productData);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getById(@PathVariable Long id){
        Product product = repository.findById(id).get();
        return new ProductResponseDTO(product);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        repository.deleteById(id);
    }


    @GetMapping("/all")
    public List<ProductResponseDTO> getAll(){
        List<ProductResponseDTO> productsList = repository.findAll().stream().map(ProductResponseDTO::new).toList();
        return productsList;
    }
}
