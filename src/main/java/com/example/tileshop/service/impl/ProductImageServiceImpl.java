package com.example.tileshop.service.impl;

import com.example.tileshop.repository.ProductImageRepository;
import com.example.tileshop.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productimageRepository;

}