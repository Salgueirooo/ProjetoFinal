package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.BakeryRequestDTO;
import com.example.sistemagestao.dto.BakeryResponseDTO;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

    @Transactional
    public void add(BakeryRequestDTO data) {
        if (bakeryRepository.existsByName(data.name())) {
            throw new EntityExistsException("Já existe uma Pastelaria com esse nome.");
        }

        String uploadDir = "uploads/bakeries/";
        String fileName = UUID.randomUUID() + "_" + data.logo().getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(data.logo().getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao guardar imagem do logotipo", e);
        }

        String logoPath = "/uploads/bakeries/" + fileName;

        Bakery bakery = new Bakery(data, logoPath);
        bakeryRepository.save(bakery);

        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<Stock> stocks = ingredients
                .stream()
                .map(ingredient -> new Stock(ingredient, bakery, 0.0))
                .toList();

        stockRepository.saveAll(stocks);

        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            orderService.initialize(bakery.getId(), user);
        }
    }

    @Transactional
    public void update(Long id, BakeryRequestDTO newData) {
        Bakery bakery = bakeryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        if (bakeryRepository.existsByName(newData.name())) {
            throw new EntityExistsException("Já existe uma Pastelaria com esse nome.");
        }

        String logoPath = bakery.getLogo();

        MultipartFile newLogo = newData.logo();
        if (newLogo != null && !newLogo.isEmpty()) {
            String uploadDir = "uploads/bakeries/";
            String fileName = UUID.randomUUID() + "_" + newLogo.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                if (logoPath != null && !logoPath.isBlank()) {
                    Path oldFile = Paths.get("." + logoPath);
                    if (Files.exists(oldFile)) {
                        Files.delete(oldFile);
                    }
                }

                Files.copy(newLogo.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                logoPath = "/uploads/bakeries/" + fileName;

            } catch (IOException e) {
                throw new RuntimeException("Erro ao atualizar imagem do logotipo", e);
            }
        }

        bakery.updateBakery(newData, logoPath);
        bakeryRepository.save(bakery);
    }

    @Transactional
    public void delete(Long id) {
        Bakery bakery = bakeryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        if(stockRepository.existsByBakeryIdAndQuantityGreaterThan(id, 0.0))
            throw new IllegalStateException("Existe stock de ingredientes nesta pastelaria.");

        String logoPath = bakery.getLogo();
        if (logoPath != null && !logoPath.isBlank()) {
            try {
                Path filePath = Paths.get("." + logoPath);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                throw new RuntimeException("Erro ao eliminar o logotipo da pastelaria.", e);
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

        bakeryRepository.delete(bakery);
    }

    public List<BakeryResponseDTO> getAll() {
        return bakeryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(BakeryResponseDTO::new)
                .toList();
    }
}
