package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.ProductReviewRequestDTO;
import com.example.sistemagestao.dto.ProductReviewResponseDTO;
import com.example.sistemagestao.repositories.OrderDetailsRepository;
import com.example.sistemagestao.repositories.ProductRepository;
import com.example.sistemagestao.repositories.ProductReviewRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private ProductReviewRepository productReviewRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private SystemConfigService systemConfigService;

    @Transactional
    public void addReview(ProductReviewRequestDTO data, User user) {
        OrderDetails orderDetails = orderDetailsRepository.findById(data.orderDetailsId())
                .orElseThrow(() -> new EntityNotFoundException("Detalhes da encomenda não encontrados."));

        if(!user.equals(orderDetails.getOrder().getUser()))
            throw new AuthorizationDeniedException("Acesso negado.");

        if(productReviewRepository.existsByOrderDetails_Id(orderDetails.getId()))
            throw new EntityExistsException("Já existe uma avaliação sobre este produto da encomenda.");

        if(data.rating() < 1 || data.rating() > 5)
            throw new IllegalArgumentException("Avaliação do produto inválida (1-5).");

        if(!orderDetails.getOrder().getOrderState().equals(OrderStates.DELIVERED))
            throw new IllegalStateException("Não é possível fazer uma Avaliação sobre esta encomenda.");

        long daysBetween = ChronoUnit.DAYS.between(orderDetails.getOrder().getDate(), LocalDateTime.now());

        int maxReviewDays = systemConfigService.getInt("MAX_REVIEW_DAYS", 7);
        if (daysBetween > maxReviewDays) {
            throw new IllegalStateException(
                    "O prazo para avaliação do produto expirou."
            );
        }

        ProductReview productReview = new ProductReview(orderDetails, data.rating(), data.review());

        productReviewRepository.save(productReview);

        productService.updateProductAverageRating(orderDetails.getProduct().getId());

        notificationService.sendToRole(
                "ROLE_ADMIN",
                "Nova avaliação ao produto " + orderDetails.getProduct().getName() + "! Mais informações ",
                "aqui.",
                orderDetails.getOrder().getBakery(),
                List.of("/home/" + orderDetails.getOrder().getBakery().getId() + "/" + NotificationService.FrontendPath.SearchProducts.getPath()
                        + "?product-name=" + orderDetails.getProduct().getName())
        );
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        ProductReview productReview = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada."));

        Product product = productReview.getOrderDetails().getProduct();

        productReviewRepository.delete(productReview);

        productService.updateProductAverageRating(product.getId());
    }

    public List<ProductReviewResponseDTO> getProductReviews(Long productId) {
        if (!productRepository.existsById(productId))
            throw new EntityNotFoundException("Produto não encontrado.");

        return productReviewRepository.findAllByOrderDetails_Product_IdOrderByDateTimeDesc(productId)
                .stream()
                .map(ProductReviewResponseDTO::new)
                .toList();
    }

    public boolean wasReviewed(Long orderDetailsId){
        return productReviewRepository.existsByOrderDetails_Id(orderDetailsId);
    }
}
