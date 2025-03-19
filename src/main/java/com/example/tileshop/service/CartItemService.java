package com.example.tileshop.service;

import com.example.tileshop.domain.entity.CartItem;

import java.util.List;

public interface CartItemService {
    List<CartItem> getAll();
}