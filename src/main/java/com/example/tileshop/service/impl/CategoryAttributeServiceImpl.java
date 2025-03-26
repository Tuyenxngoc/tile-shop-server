package com.example.tileshop.service.impl;

import com.example.tileshop.repository.CategoryAttributeRepository;
import com.example.tileshop.service.CategoryAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryAttributeServiceImpl implements CategoryAttributeService {
    private final CategoryAttributeRepository categoryattributeRepository;

}