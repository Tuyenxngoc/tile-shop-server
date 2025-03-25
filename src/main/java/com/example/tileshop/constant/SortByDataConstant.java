package com.example.tileshop.constant;

public enum SortByDataConstant implements SortByInterface {

    ATTRIBUTE {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "name" -> "name";
                case "createdDate" -> "createdDate";
                case "lastModifiedDate" -> "lastModifiedDate";
                default -> "id";
            };
        }
    },

    CART {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {

                default -> "id";
            };
        }
    },

    CART_ITEM {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {

                default -> "id";
            };
        }
    },

    CATEGORY {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "name" -> "name";
                default -> "id";
            };
        }
    },

    ORDER {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {

                default -> "id";
            };
        }
    },

    ORDER_ITEM {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {

                default -> "id";
            };
        }
    },

    PRODUCT {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {

                default -> "id";
            };
        }
    },

    PRODUCT_ATTRIBUTE {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {

                default -> "id";
            };
        }
    },

    PRODUCT_IMAGE {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {

                default -> "id";
            };
        }
    },

    REVIEW {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {

                default -> "id";
            };
        }
    },

    ROLE {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {

                default -> "id";
            };
        }
    },

    USER {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "username" -> "username";
                case "email" -> "email";
                default -> "id";
            };
        }
    },

}
