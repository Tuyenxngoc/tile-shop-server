package com.example.tileshop.service;

import com.example.tileshop.domain.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAll();
}