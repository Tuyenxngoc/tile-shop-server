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

    public static final class Chat {
        private static final String PRE_FIX = "/chat";

        public static final String CHAT_WITH_AI = PRE_FIX + "/ai";
    }

    public static class Stat {
        private static final String PRE_FIX = "/statistics";

        public static final String GET_DASHBOARD_STATS = PRE_FIX + "/dashboard";
        public static final String GET_TOP_SELLING_PRODUCTS = PRE_FIX + "/top-products";
        public static final String GET_TOP_CUSTOMERS = PRE_FIX + "/top-customers";
        public static final String GET_RECENT_ORDERS = PRE_FIX + "/recent-orders";
        public static final String GET_REVENUE_STATS = PRE_FIX + "/revenue";
        public static final String GET_REVENUE_BY_CATEGORY = PRE_FIX + "/revenue-by-category";
        public static final String GET_CHART_DATA = PRE_FIX + "/chart-data";
        public static final String EXPORT_CHART_DATA = PRE_FIX + "/export-chart-data";
    }

    public static class Visit {
        private static final String PRE_FIX = "/visit";

        public static final String TRACK = PRE_FIX + "/track";
    }

    public static class Payment {
        private static final String PRE_FIX = "/payment";

        public static final String VN_PAY = PRE_FIX + "/vn-pay";
        public static final String VN_PAY_RETURN = PRE_FIX + "/vn-pay-return";

        public static final String ZALO_PAY = PRE_FIX + "/zalopay";
        public static final String ZALO_PAY_RETURN = PRE_FIX + "/zalopay-return";

        public static final String PAYOS_CREATE_PAYMENT = PRE_FIX + "/payos/create-payment";
        public static final String PAYOS_WEBHOOK = PRE_FIX + "/payos/webhook";
    }

    public static class Attribute {
        private static final String PRE_FIX = "/attributes";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String GET_BY_CATEGORY_ID = PRE_FIX + "/category/{categoryId}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class Brand {
        private static final String PRE_FIX = "/brands";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String GET_BY_SLUG = PRE_FIX + "/slug/{slug}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class Cart {
        public static final String PRE_FIX = "/cart";

        public static final String GET_ALL = PRE_FIX;
        public static final String ADD_ITEM = PRE_FIX + "/add";
        public static final String UPDATE_ITEM = PRE_FIX + "/update/{productId}";
        public static final String REMOVE_ITEM = PRE_FIX + "/remove/{productId}";
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
        public static final String GET_BY_SLUG = PRE_FIX + "/slug/{slug}";
        public static final String GET_TREE = PRE_FIX + "/tree";
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

    public static class News {
        private static final String PRE_FIX = "/news";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String GET_BY_SLUG = PRE_FIX + "/slug/{slug}";
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

    public static final class Order {
        private static final String PRE_FIX = "/orders";
        private static final String PRE_FIX_ADMIN = ADMIN_URL + PRE_FIX;

        public static final class Admin {
            public static final String GET_ALL = PRE_FIX_ADMIN;
            public static final String GET_BY_ID = PRE_FIX_ADMIN + "/{id}";
            public static final String UPDATE_STATUS = PRE_FIX_ADMIN + "/{id}/status";
            public static final String UPDATE = PRE_FIX_ADMIN + "/{id}";
            public static final String DELETE = PRE_FIX_ADMIN + "/{id}";
            public static final String COUNT_BY_STATUS = PRE_FIX_ADMIN + "/count-by-status";
            public static final String EXPORT_REPORT = PRE_FIX_ADMIN + "/export-report";
            public static final String PRINT_INVOICE = PRE_FIX_ADMIN + "/{id}/invoice";
        }

        public static final class User {
            public static final String GET_ALL = PRE_FIX;
            public static final String GET_BY_ID = PRE_FIX + "/{id}";
            public static final String CREATE = PRE_FIX;
            public static final String CANCEL = PRE_FIX + "/{id}/cancel";
        }
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
        public static final String GET_BY_SLUG = PRE_FIX + "/slug/{slug}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String SEARCH = PRE_FIX + "/search";
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
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_PRODUCT_ID = PRE_FIX + "/product/{productId}";
        public static final String GET_SUMMARY_BY_PRODUCT_ID = PRE_FIX + "/summary/{productId}";
        public static final String APPROVE = PRE_FIX + "/{id}/approve";
        public static final String REJECT = PRE_FIX + "/{id}/reject";
    }

    public static class Role {
        private static final String PRE_FIX = "/roles";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class Slide {
        private static final String PRE_FIX = "/slides";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class StoreInfo {
        private static final String PRE_FIX = "/store-info";

        public static final String GET = PRE_FIX;
        public static final String UPDATE = PRE_FIX;
    }

    public static class User {
        private static final String PRE_FIX = "/users";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String TOGGLE_ACTIVE = PRE_FIX + "/{id}/toggle-active";
        public static final String UPDATE_MY_PROFILE = PRE_FIX + "/me";
    }
}
