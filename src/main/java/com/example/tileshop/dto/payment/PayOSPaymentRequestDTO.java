package com.example.tileshop.dto.payment;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOSPaymentRequestDTO {
    // Required
    private Integer orderCode;
    private Integer amount;
    private String description;
    private String cancelUrl;
    private String returnUrl;
    private String signature;

    // Optional
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private String buyerAddress;
    private Integer expiredAt;

    private List<ItemDTO> items;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDTO {
        private String name;
        private Integer quantity;
        private Integer price;
    }
}
