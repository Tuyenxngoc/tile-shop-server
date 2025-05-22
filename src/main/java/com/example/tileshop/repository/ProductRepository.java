package com.example.tileshop.repository;

import com.example.tileshop.dto.statistics.TopSellingProductDTO;
import com.example.tileshop.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :ids")
    List<Product> findAllByIdWithPessimisticLock(@Param("ids") List<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Product findProductForUpdate(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM Product p")
    int countProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = 0")
    int countOutOfStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= :threshold")
    int countLowStockProducts(@Param("threshold") int threshold);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.createdDate BETWEEN :startDate AND :endDate")
    int countProductsCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("""
                SELECT new com.example.tileshop.dto.statistics.TopSellingProductDTO(p, SUM(oi.quantity), SUM(oi.quantity * oi.priceAtTimeOfOrder))
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