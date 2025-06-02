package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.service.PayOSService;
import com.example.tileshop.service.VnPayService;
import com.example.tileshop.service.ZaloPayService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Payment")
public class PaymentController {
    VnPayService paymentService;

    ZaloPayService zaloPayService;

    PayOSService payOSService;

    @Operation(summary = "Initiate VNPay payment process")
    @GetMapping(UrlConstant.Payment.VN_PAY)
    public ResponseEntity<?> pay(@RequestParam("orderId") Long orderId, HttpServletRequest request) {
        return VsResponseUtil.success(paymentService.createPaymentUrl(orderId, request));
    }

    @Operation(summary = "Handle VNPay payment callback")
    @GetMapping(UrlConstant.Payment.VN_PAY_RETURN)
    public ResponseEntity<?> payCallbackHandler(@RequestParam("vnp_SecureHash") String receivedHash, HttpServletRequest request) {
        return VsResponseUtil.success(paymentService.handleVNPayReturn(receivedHash, request));
    }

    @Operation(summary = "Initiate ZaloPay payment process")
    @GetMapping(UrlConstant.Payment.ZALO_PAY)
    public ResponseEntity<?> zaloPay(@RequestParam("orderId") Long orderId, HttpServletRequest request) {
        return VsResponseUtil.success(zaloPayService.createPaymentUrl(orderId, request));
    }

    @Operation(summary = "Handle ZaloPay payment callback")
    @PostMapping(UrlConstant.Payment.ZALO_PAY_RETURN)
    public ResponseEntity<?> zaloPayCallbackHandler(HttpServletRequest request) {
        return VsResponseUtil.success(zaloPayService.handleZaloPayReturn(request));
    }

    @Operation(summary = "Initiate PayOS payment process")
    @GetMapping(UrlConstant.Payment.PAYOS_CREATE_PAYMENT)
    public ResponseEntity<?> createPayOSPayment(
            @RequestParam("orderId") Long orderId,
            @RequestParam("cancelUrl") String cancelUrl,
            @RequestParam("returnUrl") String returnUrl,
            HttpServletRequest request
    ) {
        return VsResponseUtil.success(payOSService.createPayment(orderId, cancelUrl, returnUrl, request));
    }

    @Operation(summary = "Handle PayOS webhook callback")
    @PostMapping(UrlConstant.Payment.PAYOS_WEBHOOK)
    public ResponseEntity<Map<String, Object>> receiveWebhook(@RequestBody String rawBody) {
        payOSService.handleWebhook(rawBody);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);

        return ResponseEntity.ok(response);
    }
}
