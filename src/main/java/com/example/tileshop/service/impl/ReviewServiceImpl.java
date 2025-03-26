package com.example.tileshop.service.impl;

import com.example.tileshop.repository.ReviewRepository;
import com.example.tileshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

}