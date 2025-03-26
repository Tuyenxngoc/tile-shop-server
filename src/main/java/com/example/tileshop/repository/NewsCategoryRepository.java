package com.example.tileshop.repository;

import com.example.tileshop.entity.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsCategoryRepository extends JpaRepository<NewsCategory, Long>, JpaSpecificationExecutor<NewsCategory> {
    boolean existsByName(String name);
}