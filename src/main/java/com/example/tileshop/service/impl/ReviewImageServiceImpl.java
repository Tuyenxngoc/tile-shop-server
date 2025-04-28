package com.example.tileshop.service.impl;

import com.example.tileshop.repository.ReviewImageRepository;
import com.example.tileshop.service.ReviewImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewImageServiceImpl implements ReviewImageService {
    private final ReviewImageRepository reviewimageRepository;
}