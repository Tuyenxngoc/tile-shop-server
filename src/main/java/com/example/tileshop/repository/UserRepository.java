package com.example.tileshop.repository;

import com.example.tileshop.dto.statistics.TopCustomerDTO;
import com.example.tileshop.entity.User;
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

    @Query("""
                SELECT new com.example.tileshop.dto.statistics.TopCustomerDTO(
                    u.id, u.fullName, u.email, COUNT(o.id), SUM(o.totalAmount), o.createdDate
                )
                FROM User u
                JOIN Order o ON o.user.id = u.id
                WHERE o.createdDate BETWEEN :start AND :end
                  AND o.status = 'COMPLETED'
                GROUP BY u.id, u.fullName, u.email
                ORDER BY SUM(o.totalAmount) DESC
            """)
    List<TopCustomerDTO> findTopCustomers(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}
