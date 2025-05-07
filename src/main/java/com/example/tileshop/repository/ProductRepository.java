package com.example.tileshop.repository;

import com.example.tileshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    default double getProductChangePercentage(LocalDate startDate, LocalDate endDate, LocalDate prevStartDate, LocalDate prevEndDate) {
        int currentCount = countProducts();
        int previousCount = currentCount - 5;
        if (previousCount == 0) return 100.0;
        return ((double) (currentCount - previousCount) / previousCount) * 100;
    }
}