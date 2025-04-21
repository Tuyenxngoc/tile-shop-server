package com.example.tileshop.repository;

import com.example.tileshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long id);

    Optional<CartItem> findByCartIdAndProductId(Long id, Long productId);

    long countByCartId(Long id);

    List<CartItem> findByCartUserId(String userId);

    void deleteByCartUserId(String userId);
}