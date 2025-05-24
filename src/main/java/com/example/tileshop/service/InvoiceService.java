package com.example.tileshop.service;

import com.example.tileshop.entity.Order;

import java.io.OutputStream;

public interface InvoiceService {
    void generateInvoice(Order order, OutputStream outputStream);

    void generateCompanyInvoice(Order order, OutputStream outputStream, String companyName, String companyAddress, String taxCode);
}
