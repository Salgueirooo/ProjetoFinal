package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.dto.ProductRequestDTO;
import com.example.sistemagestao.dto.ProductResponseDTO;
import com.example.sistemagestao.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void addProduct(ProductRequestDTO data) {
        Product productData = new Product(data);
        productRepository.save(productData);
    }

    public ProductResponseDTO getById(Long id){
        Product product = productRepository.findById(id).get();
        return new ProductResponseDTO(product);
    }

    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO newData) {
        Product product = productRepository.findById(id).get();

        product.updateProduct(newData);
        productRepository.save(product);

        return ResponseEntity.ok(product);
    }

    public List<ProductResponseDTO> getAll(){
        return productRepository.findAll().stream().map(ProductResponseDTO::new).toList();
    }
}