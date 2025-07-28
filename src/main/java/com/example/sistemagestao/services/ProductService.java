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
    public ResponseEntity<ProductResponseDTO> add(ProductRequestDTO data) {
        Category category = categoryRepository.findById(data.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        Product productData = new Product(data, category);
        Product saved = productRepository.save(productData);

        return ResponseEntity.ok(new ProductResponseDTO(saved));
    }

    public ProductResponseDTO getById(Long id){
        return new ProductResponseDTO(productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + id + " não encontrado")));
    }

    @Transactional
    public ResponseEntity<ProductResponseDTO> update(Long id, ProductRequestDTO newData) {

        Category category = null;
        if (newData.categoryId() != null) {
            category = categoryRepository.findById(newData.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + id + " não encontrado"));

        product.updateProduct(newData, category);
        productRepository.save(product);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(product);
        return ResponseEntity.ok(productResponseDTO);
    }

    @Transactional
    public ResponseEntity<ProductResponseDTO> changeStateById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto com ID " + id + " não encontrado"));

        product.toggleActive();
        productRepository.save(product);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(product);
        return ResponseEntity.ok(productResponseDTO);
    }

    public List<ProductResponseDTO> getAll(){
        return productRepository.findAllByOrderByNameAsc()
                .stream().map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllActive(){
        return productRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllInactive(){
        return productRepository.findByActiveFalseOrderByNameAsc()
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllByName(String name){
        return productRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllActiveByName(String name){
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrueOrderByNameAsc(name)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllInactiveByName(String name){
        return productRepository.findByNameContainingIgnoreCaseAndActiveFalseOrderByNameAsc(name)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllByCategory(Long categoryId){
        return productRepository.findByCategoryIdOrderByNameAsc(categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllActiveByCategory(Long categoryId){
        return productRepository.findByCategoryIdAndActiveTrueOrderByNameAsc(categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllInactiveByCategory(Long categoryId){
        return productRepository.findByCategoryIdAndActiveFalseOrderByNameAsc(categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllByNameAndCategory(String namePart, Long categoryId) {
        return productRepository.findByNameContainingIgnoreCaseAndCategoryIdOrderByNameAsc(namePart, categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllActiveByNameAndCategory(String namePart, Long categoryId) {
        return productRepository.findByNameContainingIgnoreCaseAndCategoryIdAndActiveTrueOrderByNameAsc(namePart, categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllInactiveByNameAndCategory(String namePart, Long categoryId) {
        return productRepository.findByNameContainingIgnoreCaseAndCategoryIdAndActiveFalseOrderByNameAsc(namePart, categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }
}