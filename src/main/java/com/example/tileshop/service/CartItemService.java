package com.example.tileshop.service;

import com.example.tileshop.entity.CartItem;

import java.util.List;

public interface CartItemService {
    List<CartItem> getAll();
}