package com.example.sistemagestao.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.sistemagestao.domain.Category;
import com.example.sistemagestao.domain.Product;
import com.example.sistemagestao.dto.CategoryRequestDTO;
import com.example.sistemagestao.dto.CategoryResponseDTO;
import com.example.sistemagestao.repositories.CategoryRepository;
import com.example.sistemagestao.repositories.ProductRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Value("${DEFAULT_IMAGE_URL}")
    private String imageDefault;

    @Value("${DEFAULT_IMAGE_PUBLIC_ID}")
    private String imageDefaultPublicId;

    private final String folder = "baketec/categories";

    @Transactional
    public void add(CategoryRequestDTO data) {
        if (categoryRepository.existsByName(data.name())) {
            throw new EntityExistsException("Já existe uma Categoria com esse nome.");
        }

        String imagePath = imageDefault;
        String imagePublicId = imageDefaultPublicId;

        MultipartFile image = data.image();

        if (image != null && !image.isEmpty()) {

            if (image.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Imagem demasiado grande (máx. 5MB).");
            }

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("O ficheiro enviado não é uma imagem.");
            }

            try {
                String publicId = UUID.randomUUID().toString();

                Map uploadResult = cloudinary.uploader().upload(
                        image.getBytes(),
                        ObjectUtils.asMap(
                                "folder", folder,
                                "public_id", publicId,
                                "overwrite", true
                        )
                );

                imagePath = uploadResult.get("secure_url").toString();
                imagePublicId = uploadResult.get("public_id").toString();

            } catch (IOException e) {
                throw new RuntimeException("Erro ao enviar imagem para o Cloudinary.", e);
            }
        }

        Category category = new Category(data, imagePath, imagePublicId);
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
        String imagePublicId = category.getImage_id();

        MultipartFile newImage = newData.image();

        if (newImage != null && !newImage.isEmpty()) {

            if (newImage.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Imagem demasiado grande (máx. 5MB).");
            }

            String contentType = newImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("O ficheiro enviado não é uma imagem.");
            }

            try {
                if (imagePublicId != null && !imagePublicId.equals(imageDefaultPublicId)) {
                    cloudinary.uploader().destroy(imagePublicId, ObjectUtils.emptyMap());
                }

                String newPublicId = UUID.randomUUID().toString();

                Map uploadResult = cloudinary.uploader().upload(
                        newImage.getBytes(),
                        ObjectUtils.asMap(
                                "folder", folder,
                                "public_id", newPublicId,
                                "overwrite", true
                        )
                );

                imagePath = uploadResult.get("secure_url").toString();
                imagePublicId = uploadResult.get("public_id").toString();

            } catch (IOException e) {
                throw new RuntimeException("Erro ao atualizar imagem do logotipo.", e);
            }
        }

        category.updateCategory(imagePath, imagePublicId);
        categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada."));

        if (category.getImage_id() != null && !category.getImage_id().equals(imageDefaultPublicId)) {

            try {
                cloudinary.uploader().destroy(
                        category.getImage_id(),
                        ObjectUtils.emptyMap()
                );
            } catch (Exception e) {
                throw new RuntimeException("Erro ao eliminar imagem do Cloudinary.", e);
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
