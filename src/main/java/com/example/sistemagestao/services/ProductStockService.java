package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.IngredientResponseDTO;
import com.example.sistemagestao.dto.IngredientStockCheckDTO;
import com.example.sistemagestao.dto.ProductStockCheckDTO;
import com.example.sistemagestao.dto.ProductStockResponseDTO;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStockService {

    @Autowired
    private BakeryRepository bakeryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductStockRepository productStockRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void initialize(Long productId) {
        List<Bakery> allBakeries = bakeryRepository.findAll();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        productStockRepository.saveAll(allBakeries.stream()
                .map(bakery -> new ProductStock(product, bakery, 0))
                .toList());
    }

    @Transactional
    public void update(Long productId, Long bakeryId, int quantity){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        Bakery bakery = bakeryRepository.findById(bakeryId)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        if (quantity < 0)
            throw new IllegalArgumentException("A quantidade deve ser maior que 0.");

        ProductStock productStock = productStockRepository.findByProductIdAndBakeryId(product.getId(), bakery.getId());

        if (productStock != null) {
            productStock.setQuantity(quantity);
            productStockRepository.save(productStock);

            notificationService.sendToRole(
                    "ROLE_CONFECTIONER",
                    "O stock do ingrediente " + productStock.getProduct().getName() + " foi atualizado!\nInformações disponíveis ",
                    "aqui.",
                    productStock.getBakery(),
                    List.of("/home/" + productStock.getBakery().getId() + "/" + NotificationService.FrontendPath.ManageProductStock.getPath())
            );
        }
    }

    @Transactional
    public void addStock(Long productId, Long bakeryId, int quantity){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        Bakery bakery = bakeryRepository.findById(bakeryId)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        if (quantity < 0)
            throw new IllegalArgumentException("A quantidade deve ser maior que 0.");

        ProductStock productStock = productStockRepository.findByProductIdAndBakeryId(product.getId(), bakery.getId());

        if (productStock != null) {
            productStock.setQuantity(productStock.getQuantity() + quantity);
            productStockRepository.save(productStock);

            notificationService.sendToRole(
                    "ROLE_CONFECTIONER",
                    "O stock do ingrediente " + productStock.getProduct().getName() + " foi atualizado!\nInformações disponíveis ",
                    "aqui.",
                    productStock.getBakery(),
                    List.of("/home/" + productStock.getBakery().getId() + "/" + NotificationService.FrontendPath.ManageProductStock.getPath())
            );
        }
    }

    public List<ProductStockResponseDTO> getAllByBakery(Long bakeryId){
        return productStockRepository.findAllByBakeryIdOrderByProductNameAsc(bakeryId)
                .stream()
                .map(ProductStockResponseDTO::new)
                .toList();
    }

    public List<ProductStockCheckDTO> verifyStockForOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new EntityNotFoundException("Encomenda não encontrada.");
        }

        List<OrderDetails> orderDetails = orderDetailsRepository.findAllByOrderId(orderId);
        if (orderDetails.isEmpty()) {
            throw new EntityNotFoundException("Detalhes da encomenda não encontrados.");
        }

        List<ProductStockCheckDTO> result = new ArrayList<>();

        for (OrderDetails orderDetail : orderDetails) {
            ProductStock productStock = productStockRepository.findByProductIdAndBakeryId(
                    orderDetail.getProduct().getId(),
                    orderDetail.getOrder().getBakery().getId()
            );

            int available = (productStock != null) ? productStock.getQuantity() : 0;
            int required = orderDetail.getQuantity();

            result.add(new ProductStockCheckDTO(
                    orderDetail.getProduct().getId(),
                    orderDetail.getProduct().getName(),
                    required,
                    available,
                    available >= required
            ));
        }

        return result;
    }

    public boolean isStockSufficientForOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new EntityNotFoundException("Encomenda não encontrada.");
        }

        List<OrderDetails> orderDetails = orderDetailsRepository.findAllByOrderId(orderId);
        if (orderDetails.isEmpty()) {
            throw new EntityNotFoundException("Detalhes da encomenda não encontrados.");
        }

        for (OrderDetails orderDetail : orderDetails) {
            ProductStock productStock = productStockRepository.findByProductIdAndBakeryId(
                    orderDetail.getProduct().getId(),
                    orderDetail.getOrder().getBakery().getId()
            );

            int available = (productStock != null) ? productStock.getQuantity() : 0;
            int required = orderDetail.getQuantity();

            if (available < required) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public void updateStockAfterUse(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new EntityNotFoundException("Encomenda não encontrada.");
        }

        List<OrderDetails> orderDetails = orderDetailsRepository.findAllByOrderId(orderId);
        if (orderDetails.isEmpty()) {
            throw new EntityNotFoundException("Detalhes da encomenda não encontrados.");
        }

        if (!isStockSufficientForOrder(orderId)) {
            throw new IllegalStateException("Não existe stock suficiente para responder a esta encomenda.");
        }

        for (OrderDetails orderDetail : orderDetails) {
            ProductStock productStock = productStockRepository.findByProductIdAndBakeryId(
                    orderDetail.getProduct().getId(),
                    orderDetail.getOrder().getBakery().getId()
            );
            if (productStock == null) {
                throw new EntityNotFoundException(
                        "Stock não encontrado para ingrediente '" + orderDetail.getProduct().getName() + "'."
                );
            }
            productStock.setQuantity(productStock.getQuantity() - orderDetail.getQuantity());
            productStockRepository.save(productStock);
        }
    }
}
