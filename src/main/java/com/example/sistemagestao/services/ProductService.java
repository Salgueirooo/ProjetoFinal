package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Category;
import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.dto.ProductDetailsResponseDTO;
import com.example.sistemagestao.dto.ProductRequestDTO;
import com.example.sistemagestao.dto.ProductResponseDTO;
import com.example.sistemagestao.dto.ProductReviewResponseDTO;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private ProducedRecipeRepository producedRecipeRepository;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Transactional
    public void add(ProductRequestDTO data) {
        Category category = categoryRepository.findById(data.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada."));

        if (productRepository.existsByName(data.name())) {
            throw new EntityExistsException("Já existe um Produto com esse nome.");
        }

        if(data.discount() < 0 || data.discount() > 100) {
            throw new IllegalStateException("Valor inválido para o Desconto (0-100).");
        }

        Product productData = new Product(data, category);
        productRepository.save(productData);
    }

    public ProductDetailsResponseDTO getDetailsById(Long id){

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        List<ProductReviewResponseDTO> reviews = productReviewRepository.findAllByOrderDetails_Product_IdOrderByDateTimeDesc(id)
                .stream()
                .map(ProductReviewResponseDTO::new)
                .toList();

        ProductResponseDTO productDTO = new ProductResponseDTO(product);

        return new ProductDetailsResponseDTO(productDTO, reviews);
    }

    @Transactional
    public void update(Long id, ProductRequestDTO newData) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        if(newData.name().equalsIgnoreCase(product.getName()) || newData.name().equalsIgnoreCase("")) {
            Category category = null;
            if (newData.categoryId() != null) {
                category = categoryRepository.findById(newData.categoryId())
                        .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada."));
            }

            if (newData.discount() < 0 || newData.discount() > 100) {
                throw new IllegalStateException("Valor inválido para o Desconto (0-100).");
            }

            if (newData.price() < 0)
                throw new IllegalStateException("O Preço deve ser um número positivo.");

            product.updateProduct(newData, category);

        } else {
            if (productRepository.existsByName(newData.name())) {
                throw new EntityExistsException("Já existe um Produto com esse nome.");
            }

            if (recipeRepository.existsByProductId(product.getId()))
                throw new EntityExistsException("Este produto já tem uma Receita associada.");

            if (producedRecipeRepository.existsByRecipe_Product_Id(product.getId()))
                throw new EntityExistsException("Já foram produzidas receitas deste Produto.");

            if (orderDetailsRepository.existsByProductId(product.getId()))
                throw new EntityExistsException("Este Produto já foi encomendado.");

            product.setName(newData.name());
        }

        productRepository.save(product);
    }

    @Transactional
    public void updateProductAverageRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        Double avgRating = productReviewRepository.getAverageRatingByProductId(productId);
        product.setRating(avgRating != null ? avgRating : 0.0);

        productRepository.save(product);
    }

    @Transactional
    public void changeStateById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        product.toggleActive();
        productRepository.save(product);
    }

    public List<ProductResponseDTO> getAll(){
        return productRepository.findAllByOrderByNameAsc()
                .stream()
                .map(ProductResponseDTO::new)
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
        if(!categoryRepository.existsById(categoryId))
            throw new EntityNotFoundException("Categoria não encontrada.");

        return productRepository.findByCategoryIdOrderByNameAsc(categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllActiveByCategory(Long categoryId){
        if(!categoryRepository.existsById(categoryId))
            throw new EntityNotFoundException("Categoria não encontrada.");

        return productRepository.findByCategoryIdAndActiveTrueOrderByNameAsc(categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllInactiveByCategory(Long categoryId){
        if(!categoryRepository.existsById(categoryId))
            throw new EntityNotFoundException("Categoria não encontrada.");

        return productRepository.findByCategoryIdAndActiveFalseOrderByNameAsc(categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllByNameAndCategory(String namePart, Long categoryId) {
        if(!categoryRepository.existsById(categoryId))
            throw new EntityNotFoundException("Categoria não encontrada.");

        return productRepository.findByNameContainingIgnoreCaseAndCategoryIdOrderByNameAsc(namePart, categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllActiveByNameAndCategory(String namePart, Long categoryId) {
        if(!categoryRepository.existsById(categoryId))
            throw new EntityNotFoundException("Categoria não encontrada.");

        return productRepository.findByNameContainingIgnoreCaseAndCategoryIdAndActiveTrueOrderByNameAsc(namePart, categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getAllInactiveByNameAndCategory(String namePart, Long categoryId) {
        if(!categoryRepository.existsById(categoryId))
            throw new EntityNotFoundException("Categoria não encontrada.");
        return productRepository.findByNameContainingIgnoreCaseAndCategoryIdAndActiveFalseOrderByNameAsc(namePart, categoryId)
                .stream()
                .map(ProductResponseDTO::new)
                .toList();
    }
}