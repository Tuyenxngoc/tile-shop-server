package com.example.tileshop.service.impl;

import com.example.tileshop.domain.entity.CartItem;
import com.example.tileshop.repository.CartItemRepository;
import com.example.tileshop.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartitemRepository;

    @Override
    public List<CartItem> getAll() {
        return cartitemRepository.findAll();
    }
}