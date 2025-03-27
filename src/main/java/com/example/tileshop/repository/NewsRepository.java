package com.example.tileshop.repository;

import com.example.tileshop.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    boolean existsBySlug(String slug);

    Optional<News> findBySlug(String slug);
}