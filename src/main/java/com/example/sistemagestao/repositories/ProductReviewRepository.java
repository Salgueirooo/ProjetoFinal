package com.example.sistemagestao.repositories;

import com.example.sistemagestao.domain.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    @Query("""
        SELECT AVG(r.rating)
        FROM ProductReview r
        WHERE r.orderDetails.product.id = :productId
    """)
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    List<ProductReview> findAllByOrderDetails_Product_IdOrderByDateTimeDesc(Long productId);

    boolean existsByOrderDetails_Id(Long orderDetailsId);
}
