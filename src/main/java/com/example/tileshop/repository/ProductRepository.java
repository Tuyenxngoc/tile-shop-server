package com.example.tileshop.repository;

import com.example.tileshop.dto.statistics.TopSellingProductDTO;
import com.example.tileshop.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsBySlug(String slug);

    Optional<Product> findBySlug(String slug);

    @Query("SELECT COUNT(p) FROM Product p")
    int countProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = 0")
    int countOutOfStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= :threshold")
    int countLowStockProducts(@Param("threshold") int threshold);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.createdDate BETWEEN :startDate AND :endDate")
    int countProductsCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("""
                SELECT new com.example.tileshop.dto.statistics.TopSellingProductDTO(
                    p.id, p.name, p.slug, p.price, p.discountPercentage, p.stockQuantity,
                    p.averageRating, p.imageUrl, p.category,
                    SUM(oi.quantity), SUM(oi.totalPrice)
                )
                FROM OrderItem oi
                JOIN oi.product p
                JOIN oi.order o
                WHERE (:startDate IS NULL OR o.createdDate >= :startDate)
                  AND (:endDate IS NULL OR o.createdDate <= :endDate)
                GROUP BY p.id
                ORDER BY SUM(oi.quantity) DESC
            """)
    List<TopSellingProductDTO> findTopSellingProducts(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}