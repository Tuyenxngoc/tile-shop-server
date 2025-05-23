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

    BRAND {
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

    CATEGORY_ATTRIBUTE {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                default -> "id";
            };
        }
    },

    NEWS {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "title" -> "title";
                case "slug" -> "slug";
                case "viewCount" -> "view_count";
                case "createdDate" -> "createdDate";
                case "lastModifiedDate" -> "lastModifiedDate";
                case "category" -> "category_id";
                default -> "id";
            };
        }
    },

    NEWS_CATEGORY {
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

    ORDER {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "status" -> "status";
                case "totalAmount" -> "totalAmount";
                case "paymentStatus" -> "paymentStatus";
                case "paymentTime" -> "paymentTime";
                case "deliveryMethod" -> "deliveryMethod";
                case "createdDate" -> "createdDate";
                case "lastModifiedDate" -> "lastModifiedDate";
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
                case "name" -> "name";
                case "price" -> "price";
                case "discountPercentage" -> "discountPercentage";
                case "stockQuantity" -> "stockQuantity";
                case "averageRating" -> "averageRating";
                case "slug" -> "slug";
                case "createdDate" -> "createdDate";
                case "lastModifiedDate" -> "lastModifiedDate";
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
                case "rating" -> "rating";
                case "comment" -> "comment";
                case "username" -> "username";
                case "status" -> "status";
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
