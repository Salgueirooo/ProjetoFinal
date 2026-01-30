package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Category;
import com.example.sistemagestao.domain.OrderDetails;
import com.example.sistemagestao.domain.OrderStates;
import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.dto.ProductDetailsResponseDTO;
import com.example.sistemagestao.dto.ProductRequestDTO;
import com.example.sistemagestao.dto.ProductResponseDTO;
import com.example.sistemagestao.dto.ProductReviewResponseDTO;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductReviewRepository productReviewRepository;
    @Autowired
    private ProductStockService productStockService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

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

        String imagePath = "/uploads/no-photo.jpg";
        MultipartFile image = data.image();

        if (image != null && !image.isEmpty()) {

            if (image.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Imagem demasiado grande (máx. 5MB).");
            }

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("O ficheiro enviado não é uma imagem.");
            }

            String uploadDir = "uploads/products/";
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(image.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                throw new RuntimeException("Erro ao guardar imagem do logotipo.", e);
            }

            imagePath = "/uploads/products/" + fileName;
        }

        Product productData = new Product(data, category, imagePath);
        productRepository.save(productData);

        productStockService.initialize(productData.getId());
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

        Category category = null;
        if (newData.categoryId() != null) {
            category = categoryRepository.findById(newData.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada."));
        }

        if (newData.discount() != null && (newData.discount() < 0 || newData.discount() > 100)) {
            throw new IllegalStateException("Valor inválido para o Desconto (0-100).");
        }

        if (newData.price() != null && newData.price() < 0)
            throw new IllegalStateException("O Preço deve ser um número positivo.");

        String logoPath = product.getImage();

        MultipartFile newLogo = newData.image();
        if (newLogo != null && !newLogo.isEmpty()) {

            if (newLogo.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Imagem demasiado grande (máx. 5MB).");
            }

            String contentType = newLogo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("O ficheiro enviado não é uma imagem.");
            }

            String uploadDir = "uploads/products/";
            String fileName = UUID.randomUUID() + "_" + newLogo.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                if (logoPath != null && !logoPath.isBlank() && !logoPath.equals("/uploads/no-photo.jpg")) {
                    Path oldFile = Paths.get("." + logoPath);
                    if (Files.exists(oldFile)) {
                        Files.delete(oldFile);
                    }
                }

                Files.copy(newLogo.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                logoPath = "/uploads/products/" + fileName;

            } catch (IOException e) {
                throw new RuntimeException("Erro ao atualizar imagem do logotipo", e);
            }
        }

        Integer currentDiscount = product.getDiscount();

        product.updateProduct(newData, category, logoPath);
        productRepository.save(product);

        if (newData.discount() != null &&  currentDiscount < newData.discount()) {
            notificationService.sendAll("O produto '" + product.getName() + "' está agora em promoção!");
        }
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

        if (product.getActive()) {
            orderDetailsRepository.deleteAllByProductIdAndOrderState(product.getId(), OrderStates.INCART);
        }

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