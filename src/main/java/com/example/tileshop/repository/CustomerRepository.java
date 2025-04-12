package com.example.tileshop.repository;

import com.example.tileshop.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserId(String userId);

    @Query("SELECT c.id FROM Customer c WHERE c.user.id = :userId")
    Long findCustomerIdByUserId(@Param("userId") String userId);
}