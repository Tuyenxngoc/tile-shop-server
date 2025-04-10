package com.example.tileshop.repository;

import com.example.tileshop.constant.ReviewStatus;
import com.example.tileshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    List<Review> findByProductSlugAndStatus(String slug, ReviewStatus reviewStatus);
}