package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Category;
import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.dto.ProductRequestDTO;
import com.example.sistemagestao.dto.ProductResponseDTO;
import com.example.sistemagestao.repositories.CategoryRepository;
import com.example.sistemagestao.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public void addProduct(ProductRequestDTO data) {
        Category category = categoryRepository.findById(data.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        Product productData = new Product(data, category);
        productRepository.save(productData);
    }

    public ProductResponseDTO getById(Long id){
        //Product product = productRepository.findById(id)
          //      .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + id + " não encontrado"));
        //return new ProductResponseDTO(product);

        return new ProductResponseDTO(productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + id + " não encontrado")));
    }

    @Transactional
    public ResponseEntity<Product> updateProduct(Long id, ProductRequestDTO newData) {
        Category category = categoryRepository.findById(newData.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + id + " não encontrado"));

        product.updateProduct(newData, category);
        productRepository.save(product);

        return ResponseEntity.ok(product);
    }

    @Transactional
    public void deleteById(Long id){
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto com ID " + id + " não encontrado");
        }
        productRepository.deleteById(id);
    }

    public List<ProductResponseDTO> getAllProducts(){
        return productRepository.findAllByOrderByNameAsc()
                .stream().map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllProductsByName(String name){
        return productRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllProductsByCategory(Long categoryId){
        return productRepository.findByCategoryIdOrderByNameAsc(categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllProductsByNameAndCategory(String namePart, Long categoryId) {
        return productRepository.findByNameContainingIgnoreCaseAndCategoryIdOrderByNameAsc(namePart, categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }
}