package com.example.tileshop.service.impl;

import com.example.tileshop.entity.News;
import com.example.tileshop.repository.NewsRepository;
import com.example.tileshop.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;

    @Override
    public List<News> getAll() {
        return newsRepository.findAll();
    }
}