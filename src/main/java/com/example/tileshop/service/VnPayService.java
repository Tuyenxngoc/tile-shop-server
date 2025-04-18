package com.example.tileshop.service;

import jakarta.servlet.http.HttpServletRequest;

public interface VnPayService {
    String createPaymentUrl(HttpServletRequest request, long amount);
}
