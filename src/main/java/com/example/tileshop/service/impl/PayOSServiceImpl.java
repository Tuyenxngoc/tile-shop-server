package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.constant.PaymentStatus;
import com.example.tileshop.dto.order.PaymentResponseDTO;
import com.example.tileshop.dto.payment.PayOSPaymentRequestDTO;
import com.example.tileshop.entity.Order;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.exception.NotFoundException;
import com.example.tileshop.repository.OrderRepository;
import com.example.tileshop.service.PayOSService;
import com.example.tileshop.util.PaymentUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayOSServiceImpl implements PayOSService {
    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Value("${payos.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    private final OrderRepository orderRepository;

    private final ObjectMapper mapper;

    @Override
    public PaymentResponseDTO createPayment(Long orderId, String cancelUrl, String returnUrl, HttpServletRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Order.ERR_NOT_FOUND_ID, orderId));

        // Kiểm tra trạng thái thanh toán
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException(ErrorMessage.Order.ERR_ALREADY_PAID);
        }

        // Kiểm tra trạng thái đơn hàng đã huỷ chưa
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException(ErrorMessage.Order.ERR_ORDER_CANCELLED);
        }

        int amount = (int) Math.round(order.getTotalAmount());
        String orderCode = PaymentUtil.getRandomNumber(8);
        String description = "Thanh toan DH #" + orderCode;

        // Tạo danh sách sản phẩm
        List<PayOSPaymentRequestDTO.ItemDTO> items = order.getOrderItems().stream()
                .map(item -> PayOSPaymentRequestDTO.ItemDTO.builder()
                        .name(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price((int) Math.round(item.getPriceAtTimeOfOrder()))
                        .build())
                .toList();

        String rawData = "amount=" + amount +
                "&cancelUrl=" + cancelUrl +
                "&description=" + description +
                "&orderCode=" + orderCode +
                "&returnUrl=" + returnUrl;
        String signature = PaymentUtil.generateSignature(rawData, checksumKey);

        // Tạo DTO gửi lên PayOS
        PayOSPaymentRequestDTO paymentRequestDTO = PayOSPaymentRequestDTO.builder()
                .orderCode(Integer.parseInt(orderCode))
                .amount(amount)
                .description(description)
                .cancelUrl(cancelUrl)
                .returnUrl(returnUrl)
                .signature(signature)
                .buyerName(order.getRecipientName())
                .buyerEmail(order.getRecipientEmail())
                .buyerPhone(order.getRecipientPhone())
                .buyerAddress(order.getShippingAddress())
                .expiredAt((int) (System.currentTimeMillis() / 1000 + 900)) // expires after 15 minutes
                .items(items)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PayOSPaymentRequestDTO> entity = new HttpEntity<>(paymentRequestDTO, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            log.warn("PayOS response body: {}", body);

            Object codeObj = body.get("code");

            // PayOS trả về code == 00 khi thành công
            if ("00".equals(String.valueOf(codeObj))) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                String checkoutUrl = (String) data.get("checkoutUrl");

                // Lưu orderCode vào đơn hàng để xử lý webhook
                order.setTransactionId(orderCode);
                orderRepository.save(order);

                return PaymentResponseDTO.builder()
                        .status("ok")
                        .message("success")
                        .URL(checkoutUrl)
                        .build();
            } else {
                // Trường hợp code != 0 => lỗi từ PayOS
                String desc = (String) body.getOrDefault("desc", "Unknown error");
                return PaymentResponseDTO.builder()
                        .status("error")
                        .message("PayOS error: " + desc)
                        .URL(null)
                        .build();
            }
        } else {
            return PaymentResponseDTO.builder()
                    .status("error")
                    .message("Request failed or no response")
                    .URL(null)
                    .build();
        }
    }

    @Override
    public void handleWebhook(String rawBody) {
        if (rawBody == null || rawBody.trim().isEmpty()) {
            log.warn("Received empty or null webhook body");
            return;
        }

        if (rawBody.length() > 10_000) {
            log.warn("Webhook body too large, possible abuse. Size: {}", rawBody.length());
            return;
        }

        log.info("Received PayOS webhook rawBody: {}", rawBody);
        try {
            Map<String, Object> payload = mapper.readValue(rawBody, Map.class);

            if (!payload.containsKey("signature") || !payload.containsKey("data")) {
                log.warn("Webhook payload missing required fields: {}", payload);
                return;
            }

            String signature = (String) payload.get("signature");
            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            // Validate structure of data
            if (data == null) {
                log.warn("Invalid data structure in webhook: {}", data);
                return;
            }

            // Tạo rawData string theo đúng thứ tự key alpha từ data
            List<String> keys = new ArrayList<>(data.keySet());
            Collections.sort(keys);

            StringBuilder rawDataBuilder = new StringBuilder();
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                Object value = data.get(key);
                rawDataBuilder.append(key).append("=").append(value);
                if (i < keys.size() - 1) {
                    rawDataBuilder.append("&");
                }
            }

            String rawData = rawDataBuilder.toString();
            String expectedSignature = PaymentUtil.generateSignature(rawData, checksumKey);

            if (!expectedSignature.equalsIgnoreCase(signature)) {
                log.error("Invalid PayOS webhook signature. Expected: {}, Received: {}. Payload: {}",
                        expectedSignature, signature, payload);
                return;
            }

            if (!data.containsKey("orderCode") || !data.containsKey("code")) {
                log.warn("Missing required fields in webhook data: {}", data);
                return;
            }

            // Lấy orderCode, responseCode
            String orderCode = String.valueOf(data.get("orderCode"));
            String responseCode = (String) data.get("code");

            Order order = orderRepository.findByTransactionId(orderCode).orElse(null);
            if (order == null) {
                log.warn("Order not found for transactionId: {}", orderCode);
                return;
            }

            // Cập nhật trạng thái
            if ("00".equals(responseCode)) {
                order.setPaymentStatus(PaymentStatus.PAID);
                String transactionDateStr = (String) data.get("transactionDateTime");
                if (transactionDateStr != null && !transactionDateStr.isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime payDateTime = LocalDateTime.parse(transactionDateStr, formatter);
                    order.setPaymentTime(payDateTime);
                } else {
                    order.setPaymentTime(LocalDateTime.now());
                }
            } else {
                order.setPaymentStatus(PaymentStatus.FAILED);
            }

            order.setResponseCode(responseCode);
            orderRepository.save(order);
        } catch (Exception e) {
            log.error("Error while processing PayOS webhook. Exception: ", e);
        }
    }
}
