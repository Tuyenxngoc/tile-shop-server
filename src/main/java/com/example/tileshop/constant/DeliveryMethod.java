package com.example.tileshop.constant;

public enum DeliveryMethod {
    STORE_PICKUP("store_pickup"),  // Nhận hàng tại cửa hàng
    HOME_DELIVERY("home_delivery"); // Giao hàng tận nơi

    private final String value;

    DeliveryMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
