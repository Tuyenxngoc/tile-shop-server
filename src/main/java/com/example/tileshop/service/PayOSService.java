package com.example.tileshop.service;

import com.example.tileshop.dto.order.PaymentResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface PayOSService {
    PaymentResponseDTO createPayment(Long orderId, String cancelUrl, String returnUrl, HttpServletRequest request);

    void handleWebhook(String rawBody);
}
