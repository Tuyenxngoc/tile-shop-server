package com.example.tileshop.service.impl;

import com.example.tileshop.entity.Customer;
import com.example.tileshop.repository.CustomerRepository;
import com.example.tileshop.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }
}