package com.example.tileshop.constant;

public enum OrderStatus {
    PENDING("Chờ xác nhận"),
    PROCESSING("Đang xử lý"),
    SHIPPED("Đang giao"),
    DELIVERED("Đã giao"),
    CANCELLED("Đã hủy");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
