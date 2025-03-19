package com.example.tileshop.service;

import com.example.tileshop.domain.entity.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> getAll();
}