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
    public void add(CategoryRequestDTO data) {
        Category category = new Category(data);
        categoryRepository.save(category);
    }

    public List<CategoryResponseDTO> getAll() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream().map(CategoryResponseDTO::new)
                .toList();
   }

    public CategoryResponseDTO getById(Long id) {
        return new CategoryResponseDTO(categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria com ID " + id + " não encontrada")));
    }

    @Transactional
    public void update(Long id, CategoryRequestDTO newData) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria com ID " + id + " não encontrada"));

        category.updateCategory(newData);
        categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria com ID " + id + " não encontrada"));

        List<Product> products = productRepository.findByCategoryId(id);
        for (Product product : products) {
            product.setCategory(null);
        }
        productRepository.saveAll(products);

        categoryRepository.delete(category);
    }
}
