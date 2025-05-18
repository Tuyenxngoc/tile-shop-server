package com.example.tileshop.repository;

import com.example.tileshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndEmail(String username, String email);

    boolean existsByUsernameOrEmail(String username, String email);

    @Query("SELECT COUNT(c) FROM User c")
    int countCustomers();

    @Query("SELECT COUNT(c) FROM User c WHERE c.createdDate BETWEEN :startDate AND :endDate")
    int countNewCustomers(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    default double getCustomerChangePercentage(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime prevStartDate, LocalDateTime prevEndDate) {
        int currentCount = countNewCustomers(startDate, endDate);
        int previousCount = countNewCustomers(prevStartDate, prevEndDate);
        if (previousCount == 0) return 100.0;
        return ((double) (currentCount - previousCount) / previousCount) * 100;
    }
}
