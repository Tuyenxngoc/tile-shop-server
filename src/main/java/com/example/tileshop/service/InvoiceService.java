package com.example.tileshop.service;

import com.example.tileshop.entity.Order;

import java.io.OutputStream;

public interface InvoiceService {
    void generateInvoice(Order order, OutputStream outputStream);
}
