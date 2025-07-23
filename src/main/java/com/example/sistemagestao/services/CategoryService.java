package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Category;
import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.dto.CategoryRequestDTO;
import com.example.sistemagestao.dto.CategoryResponseDTO;
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
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void addCategory(CategoryRequestDTO data) {
        Category category = new Category(data);
        categoryRepository.save(category);
    }

    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream().map(CategoryResponseDTO::new)
                .toList();
   }

    public CategoryResponseDTO getCategoryById(Long id) {
        return new CategoryResponseDTO(categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria com ID " + id + " não encontrada")));
    }

    @Transactional
    public ResponseEntity<Category> updateCategory(Long id, CategoryRequestDTO newData) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria com ID " + id + " não encontrada"));

        category.updateCategory(newData);
        categoryRepository.save(category);

        return ResponseEntity.ok(category);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto com ID " + id + " não encontrado");
        }

        List<Product> products = productRepository.findByCategoryId(id);
        for (Product product : products) {
            product.setCategory(null);
        }
        productRepository.saveAll(products);

        categoryRepository.deleteById(id);
    }

}
