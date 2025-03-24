package com.example.tileshop.service.impl;

import com.example.tileshop.entity.NewsCategory;
import com.example.tileshop.repository.NewsCategoryRepository;
import com.example.tileshop.service.NewsCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsCategoryServiceImpl implements NewsCategoryService {
    private final NewsCategoryRepository newscategoryRepository;

    @Override
    public List<NewsCategory> getAll() {
        return newscategoryRepository.findAll();
    }
}