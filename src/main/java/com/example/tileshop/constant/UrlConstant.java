package com.example.tileshop.constant;

public class UrlConstant {

    public static final String ADMIN_URL = "/admin";

    public static class Auth {
        private static final String PRE_FIX = "/auth";

        public static final String LOGIN = PRE_FIX + "/login";
        public static final String LOGOUT = PRE_FIX + "/logout";
        public static final String REGISTER = PRE_FIX + "/register";
        public static final String FORGOT_PASSWORD = PRE_FIX + "/forgot-password";
        public static final String CHANGE_PASSWORD = PRE_FIX + "/change-password";
        public static final String REFRESH_TOKEN = PRE_FIX + "/refresh-token";
        public static final String GET_CURRENT_USER = PRE_FIX + "/current";
    }

    public static class Attribute {
        private static final String PRE_FIX = "/attributes";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class Cart {
        private static final String PRE_FIX = "/cart";

        public static final String GET = PRE_FIX;
        public static final String CLEAR = PRE_FIX + "/clear";
    }

    public static class CartItem {
        private static final String PRE_FIX = "/cart-items";

        public static final String ADD = PRE_FIX;
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String REMOVE = PRE_FIX + "/{id}";
    }

    public static class Category {
        private static final String PRE_FIX = "/categories";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class CategoryAttribute {
        private static final String PRE_FIX = "/category-attributes";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class Customer {
        private static final String PRE_FIX = "/customers";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class News {
        private static final String PRE_FIX = "/news";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class NewsCategory {
        private static final String PRE_FIX = "/news-categories";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class Order {
        private static final String PRE_FIX = "/orders";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE_STATUS = PRE_FIX + "/{id}/status";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class OrderItem {
        private static final String PRE_FIX = "/order-items";

        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ORDER_ID = PRE_FIX + "/order/{orderId}";
    }

    public static class Product {
        private static final String PRE_FIX = "/products";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class ProductAttribute {
        private static final String PRE_FIX = "/product-attributes";

        public static final String GET_BY_PRODUCT_ID = PRE_FIX + "/product/{productId}";
    }

    public static class ProductImage {
        private static final String PRE_FIX = "/product-images";

        public static final String UPLOAD = PRE_FIX + "/upload";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class Review {
        private static final String PRE_FIX = "/reviews";

        public static final String CREATE = PRE_FIX;
        public static final String GET_BY_PRODUCT_ID = PRE_FIX + "/product/{productId}";
    }

    public static class Role {
        private static final String PRE_FIX = "/roles";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class User {
        private static final String PRE_FIX = "/users";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String UPLOAD_IMAGES = PRE_FIX + "/upload-images";
    }

}
