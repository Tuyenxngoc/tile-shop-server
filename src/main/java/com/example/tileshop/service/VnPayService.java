package com.example.tileshop.service;

import com.example.tileshop.dto.order.PaymentResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface VnPayService {
    PaymentResponseDTO createPaymentUrl(Long amount, String bankCode, HttpServletRequest request);

    void handleVNPayReturn(HttpServletRequest request);
}
