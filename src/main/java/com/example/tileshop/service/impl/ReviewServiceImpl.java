package com.example.tileshop.service.impl;

import com.example.tileshop.entity.Review;
import com.example.tileshop.repository.ReviewRepository;
import com.example.tileshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Override
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }
}