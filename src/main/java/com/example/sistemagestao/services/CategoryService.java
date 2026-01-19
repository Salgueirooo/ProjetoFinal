package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Category;
import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.dto.CategoryRequestDTO;
import com.example.sistemagestao.dto.CategoryResponseDTO;
import com.example.sistemagestao.repositories.CategoryRepository;
import com.example.sistemagestao.repositories.ProductRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void add(CategoryRequestDTO data) {
        if (categoryRepository.existsByName(data.name())) {
            throw new EntityExistsException("Já existe uma Categoria com esse nome.");
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

            String uploadDir = "uploads/categories/";
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(
                        image.getInputStream(),
                        uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );

            } catch (IOException e) {
                throw new RuntimeException("Erro ao guardar imagem.", e);
            }

            imagePath = "/uploads/categories/" + fileName;
        }

        Category category = new Category(data, imagePath);
        categoryRepository.save(category);
    }

    public List<CategoryResponseDTO> getAll() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream().map(CategoryResponseDTO::new)
                .toList();
   }

    public CategoryResponseDTO getById(Long id) {
        return new CategoryResponseDTO(categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada.")));
    }

    @Transactional
    public void update(Long id, CategoryRequestDTO newData) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada."));

        String imagePath = category.getImage();

        MultipartFile newImage = newData.image();
        if (newImage != null && !newImage.isEmpty()) {

            if (newImage.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Imagem demasiado grande (máx. 5MB).");
            }

            String contentType = newImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("O ficheiro enviado não é uma imagem.");
            }

            String uploadDir = "uploads/categories/";
            String fileName = UUID.randomUUID() + "_" + newImage.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                if (imagePath != null && !imagePath.isBlank() && !imagePath.equals("/uploads/no-photo.jpg")) {
                    Path oldFile = Paths.get("." + imagePath);
                    if (Files.exists(oldFile)) {
                        Files.delete(oldFile);
                    }
                }

                Files.copy(newImage.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                imagePath = "/uploads/categories/" + fileName;

            } catch (IOException e) {
                throw new RuntimeException("Erro ao atualizar imagem do logotipo", e);
            }
        }

        category.updateCategory(imagePath);
        categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada."));

        String imagePath = category.getImage();
        if (imagePath != null && !imagePath.isBlank() && !imagePath.equals("/uploads/no-photo.jpg")) {
            try {
                Path filePath = Paths.get("." + imagePath);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                throw new RuntimeException("Erro ao eliminar a imagem.", e);
            }
        }

        List<Product> products = productRepository.findByCategoryId(id);
        for (Product product : products) {
            product.setCategory(null);
        }
        productRepository.saveAll(products);

        categoryRepository.delete(category);
    }
}
