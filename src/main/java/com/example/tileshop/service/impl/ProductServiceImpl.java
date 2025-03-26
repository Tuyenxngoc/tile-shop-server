package com.example.tileshop.service.impl;

import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

}