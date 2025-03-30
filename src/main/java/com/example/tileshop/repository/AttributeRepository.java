package com.example.tileshop.repository;

import com.example.tileshop.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    boolean existsByName(String name);

    List<Attribute> findAllByCategoryAttributes_Category_Id(Long categoryId);
}