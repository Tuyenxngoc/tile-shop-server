package com.example.tileshop.constant;

public class SuccessMessage {
    public static final String CREATE = "success.create";
    public static final String UPDATE = "success.update";
    public static final String DELETE = "success.delete";

    public static class Auth {
        public static final String LOGOUT = "success.auth.logout";
    }

    public static class Attribute {
    }

    public static class Brand {
    }

    public static class Cart {
        public static final String CLEAR = "success.cart.clear";
    }

    public static class CartItem {
        public static final String ADD = "success.cart-item.add";
        public static final String UPDATE = "success.cart-item.update";
        public static final String DELETE = "success.cart-item.delete";
    }

    public static class Category {
    }

    public static class CategoryAttribute {
    }

    public static class News {
    }

    public static class NewsCategory {
    }

    public static class Order {
        public static final String UPDATE_STATUS = "success.order.update-status";
        public static final String ORDER_CANCELLED = "success.order.order-cancelled";
    }

    public static class OrderItem {
    }

    public static class Product {
    }

    public static class ProductAttribute {
    }

    public static class ProductImage {
    }

    public static class Review {
        public static final String APPROVE = "success.review.approve";
        public static final String REJECT = "success.review.reject";
    }

    public static class Role {
    }

    public static class User {
        public static final String CHANGE_PASSWORD = "success.user.change-password";
        public static final String FORGOT_PASSWORD = "success.user.send.password";
        public static final String REGISTER = "success.user.register";
    }
}
