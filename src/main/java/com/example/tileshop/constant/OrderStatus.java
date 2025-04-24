package com.example.tileshop.constant;

public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    DELIVERING("Đang giao"),
    DELIVERED("Đã giao"),
    RETURNED("Trả hàng"),
    CANCELLED("Đã hủy");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
