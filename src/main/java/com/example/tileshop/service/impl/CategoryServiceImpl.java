package com.example.tileshop.service.impl;

import com.example.tileshop.domain.entity.Category;
import com.example.tileshop.repository.CategoryRepository;
import com.example.tileshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}