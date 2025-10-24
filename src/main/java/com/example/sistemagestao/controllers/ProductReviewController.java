package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.User;
import com.example.sistemagestao.dto.ProductReviewRequestDTO;
import com.example.sistemagestao.services.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/product-review")
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    @PostMapping("/add")
    public void addProductReview(@RequestBody ProductReviewRequestDTO data, @AuthenticationPrincipal User user) {
        productReviewService.addReview(data, user);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProductReview(@PathVariable Long id, @AuthenticationPrincipal User user) {
        productReviewService.deleteReview(id, user);
    }
}
