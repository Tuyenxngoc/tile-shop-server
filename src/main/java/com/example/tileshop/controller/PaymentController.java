package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.service.VnPayService;
import com.example.tileshop.util.VsResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    VnPayService paymentService;

    @GetMapping("payment/vn-pay")
    public ResponseEntity<?> pay(@RequestParam("orderId") Long orderId, HttpServletRequest request) {
        return VsResponseUtil.success(paymentService.createPaymentUrl(orderId, request));
    }

    @GetMapping("payment/vn-pay-return")
    public ResponseEntity<?> payCallbackHandler(@RequestParam("vnp_SecureHash") String receivedHash, HttpServletRequest request) {
        return VsResponseUtil.success(paymentService.handleVNPayReturn(receivedHash, request));
    }
}
