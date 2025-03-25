package com.example.tileshop.repository;

import com.example.tileshop.entity.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {
    @Modifying
    @Transactional
    void deleteByCategoryIdAndAttributeIdIn(Long categoryId, Set<Long> attributeIds);
}