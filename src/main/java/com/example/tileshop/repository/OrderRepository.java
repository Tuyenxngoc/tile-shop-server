package com.example.tileshop.repository;

import com.example.tileshop.dto.statistics.RecentOrderDTO;
import com.example.tileshop.entity.Order;
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
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByTransactionId(String vnpTxnRef);

    List<Order> findAllByUserId(String userId);

    Optional<Order> findByIdAndUserId(Long id, String userId);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersGroupByStatus();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED' AND o.createdDate BETWEEN :startDate AND :endDate")
    double getTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdDate BETWEEN :startDate AND :endDate")
    int countOrders(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'DELIVERED' AND o.createdDate BETWEEN :startDate AND :endDate")
    int countCompletedOrders(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING' AND o.createdDate BETWEEN :startDate AND :endDate")
    int countPendingOrders(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'CANCELLED' AND o.createdDate BETWEEN :startDate AND :endDate")
    int countCancelledOrders(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("""
                SELECT new com.example.tileshop.dto.statistics.RecentOrderDTO(
                    o.id, o.totalAmount, o.status, o.createdDate, u.fullName
                )
                FROM Order o
                JOIN o.user u
                ORDER BY o.createdDate DESC
            """)
    List<RecentOrderDTO> findRecentOrders(Pageable pageable);
}