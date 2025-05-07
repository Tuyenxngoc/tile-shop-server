package com.example.tileshop.repository;

import com.example.tileshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByTransactionId(String vnpTxnRef);

    List<Order> findAllByUserId(String userId);

    Optional<Order> findByIdAndUserId(Long id, String userId);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersGroupByStatus();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED' AND o.createdDate BETWEEN :startDate AND :endDate")
    double getTotalRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdDate BETWEEN :startDate AND :endDate")
    int countOrders(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'COMPLETED' AND o.createdDate BETWEEN :startDate AND :endDate")
    int countCompletedOrders(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING' AND o.createdDate BETWEEN :startDate AND :endDate")
    int countPendingOrders(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'CANCELLED' AND o.createdDate BETWEEN :startDate AND :endDate")
    int countCancelledOrders(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    default double getOrderChangePercentage(LocalDate startDate, LocalDate endDate, LocalDate prevStartDate, LocalDate prevEndDate) {
        int currentCount = countOrders(startDate, endDate);
        int previousCount = countOrders(prevStartDate, prevEndDate);
        if (previousCount == 0) return 100.0;
        return ((double) (currentCount - previousCount) / previousCount) * 100;
    }

    default double getRevenueChangePercentage(LocalDate startDate, LocalDate endDate, LocalDate prevStartDate, LocalDate prevEndDate) {
        double currentRevenue = getTotalRevenue(startDate, endDate);
        double previousRevenue = getTotalRevenue(prevStartDate, prevEndDate);
        if (previousRevenue == 0) return 100.0;
        return ((currentRevenue - previousRevenue) / previousRevenue) * 100;
    }
}