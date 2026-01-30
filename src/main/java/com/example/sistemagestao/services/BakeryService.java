package com.example.sistemagestao.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.BakeryRequestDTO;
import com.example.sistemagestao.dto.BakeryResponseDTO;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BakeryService {

    @Autowired
    private BakeryRepository bakeryRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ProducedRecipeRepository producedRecipeRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProducedRecipeService producedRecipeService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductStockRepository productStockRepository;
    @Autowired
    private Cloudinary cloudinary;

    @Value("${DEFAULT_IMAGE_URL}")
    private String imageDefault;

    @Value("${DEFAULT_IMAGE_PUBLIC_ID}")
    private String imageDefaultPublicId;

    private final String folder = "baketec/bakeries";

    @Transactional
    public void add(BakeryRequestDTO data) {
        if (bakeryRepository.existsByName(data.name())) {
            throw new EntityExistsException("Já existe uma Pastelaria com esse nome.");
        }
        String logoPath = imageDefault;
        String logoPublicId = imageDefaultPublicId;

        MultipartFile logo = data.logo();

        if (logo != null && !logo.isEmpty()) {

            if (logo.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Imagem demasiado grande (máx. 5MB).");
            }

            String contentType = logo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("O ficheiro enviado não é uma imagem.");
            }

            try {
                String publicId = UUID.randomUUID().toString();

                Map uploadResult = cloudinary.uploader().upload(
                        logo.getBytes(),
                        ObjectUtils.asMap(
                                "folder", folder,
                                "public_id", publicId,
                                "overwrite", true
                        )
                );

                logoPath = uploadResult.get("secure_url").toString();
                logoPublicId = uploadResult.get("public_id").toString();

            } catch (IOException e) {
                throw new RuntimeException("Erro ao enviar imagem para o Cloudinary.", e);
            }
        }

        Bakery bakery = new Bakery(data, logoPath, logoPublicId);
        bakeryRepository.save(bakery);

        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<Stock> stocks = ingredients
                .stream()
                .map(ingredient -> new Stock(ingredient, bakery, 0.0))
                .toList();

        stockRepository.saveAll(stocks);

        List<Product> products = productRepository.findAll();
        List<ProductStock> productStocks = products
                .stream()
                .map(product -> new ProductStock(product, bakery, 0))
                .toList();

        productStockRepository.saveAll(productStocks);

        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            orderService.initialize(bakery.getId(), user);
        }
    }

    @Transactional
    public void update(Long id, BakeryRequestDTO newData) {
        Bakery bakery = bakeryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        String logoPath = bakery.getLogo();
        String logoPublicId = bakery.getLogo_id();

        MultipartFile newLogo = newData.logo();

        if (newLogo != null && !newLogo.isEmpty()) {

            if (newLogo.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Imagem demasiado grande (máx. 5MB).");
            }

            String contentType = newLogo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("O ficheiro enviado não é uma imagem.");
            }

            try {
                if (logoPublicId != null && !logoPublicId.equals(imageDefaultPublicId)) {
                    cloudinary.uploader().destroy(logoPublicId, ObjectUtils.emptyMap());
                }

                String newPublicId = UUID.randomUUID().toString();

                Map uploadResult = cloudinary.uploader().upload(
                        newLogo.getBytes(),
                        ObjectUtils.asMap(
                                "folder", folder,
                                "public_id", newPublicId,
                                "overwrite", true
                        )
                );

                logoPath = uploadResult.get("secure_url").toString();
                logoPublicId = uploadResult.get("public_id").toString();

            } catch (IOException e) {
                throw new RuntimeException("Erro ao atualizar imagem do logotipo.", e);
            }
        }

        bakery.updateBakery(newData, logoPath, logoPublicId);
        bakeryRepository.save(bakery);
    }

    @Transactional
    public void delete(Long id) {
        Bakery bakery = bakeryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        if(stockRepository.existsByBakeryIdAndQuantityGreaterThan(id, 0.0))
            throw new IllegalStateException("Não é possível eliminar esta pastelaria: Ainda existe stock de ingredientes nesta pastelaria!");

        if(productStockRepository.existsByBakeryIdAndQuantityGreaterThan(id, 0))
            throw new IllegalStateException("Não é possível eliminar esta pastelaria: Ainda existe stock de produtos nesta pastelaria!");

        if (bakery.getLogo_id() != null && !bakery.getLogo_id().equals(imageDefaultPublicId)) {

            try {
                cloudinary.uploader().destroy(
                        bakery.getLogo_id(),
                        ObjectUtils.emptyMap()
                );
            } catch (Exception e) {
                throw new RuntimeException("Erro ao eliminar imagem do Cloudinary.", e);
            }
        }

        List<ProducedRecipe> producedRecipes = producedRecipeRepository.findByBakeryId(id);

        List<Order> orders = orderRepository.findAllByBakery_Id(bakery.getId());

        if (!producedRecipes.isEmpty()) {
            for (ProducedRecipe pr : producedRecipes) {
                producedRecipeService.cancelRecipe(pr.getId());
            }
        }

        if (!orders.isEmpty()) {
            for (Order o : orders) {
                orderService.deleteOrder(o.getId());
            }
        }

        stockRepository.deleteByBakeryId(id);
        productStockRepository.deleteByBakeryId(id);

        bakeryRepository.delete(bakery);
    }

    public List<BakeryResponseDTO> getAll() {
        return bakeryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(BakeryResponseDTO::new)
                .toList();
    }

    public String getBakeryName(Long id) {
        Bakery bakery = bakeryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        return bakery.getName();
    }
}
