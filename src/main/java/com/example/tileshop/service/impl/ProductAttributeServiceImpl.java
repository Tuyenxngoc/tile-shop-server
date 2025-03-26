package com.example.tileshop.service.impl;

import com.example.tileshop.repository.ProductAttributeRepository;
import com.example.tileshop.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductAttributeServiceImpl implements ProductAttributeService {
    private final ProductAttributeRepository productattributeRepository;

}