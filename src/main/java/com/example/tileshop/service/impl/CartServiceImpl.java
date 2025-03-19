package com.example.tileshop.service.impl;

import com.example.tileshop.entity.Cart;
import com.example.tileshop.repository.CartRepository;
import com.example.tileshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

    @Override
    public List<Cart> getAll() {
        return cartRepository.findAll();
    }
}