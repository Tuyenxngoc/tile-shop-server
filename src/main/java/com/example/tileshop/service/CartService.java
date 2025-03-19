package com.example.tileshop.service;

import com.example.tileshop.domain.entity.Cart;

import java.util.List;

public interface CartService {
    List<Cart> getAll();
}