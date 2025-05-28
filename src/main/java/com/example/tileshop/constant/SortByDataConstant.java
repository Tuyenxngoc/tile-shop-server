package com.example.tileshop.constant;

public enum SortByDataConstant implements SortByInterface {
    ATTRIBUTE {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "name" -> "name";
                case "isRequired" -> "isRequired";
                case "defaultValue" -> "defaultValue";
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
                case "slug" -> "slug";
                case "description" -> "description";
                case "logoUrl" -> "logoUrl";
                case "createdDate" -> "createdDate";
                case "lastModifiedDate" -> "lastModifiedDate";
                default -> "id";
            };
        }
    },

    CATEGORY {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "name" -> "name";
                case "slug" -> "slug";
                case "imageUrl" -> "imageUrl";
                case "description" -> "description";
                case "createdDate" -> "createdDate";
                case "lastModifiedDate" -> "lastModifiedDate";
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
                case "description" -> "description";
                case "imageUrl" -> "imageUrl";
                case "viewCount" -> "viewCount";
                case "createdDate" -> "createdDate";
                case "lastModifiedDate" -> "lastModifiedDate";
                case "category" -> "category";
                default -> "id";
            };
        }
    },

    NEWS_CATEGORY {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "name" -> "name";
                case "slug" -> "slug";
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

    USER {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "username" -> "username";
                case "email" -> "email";
                case "phoneNumber" -> "phoneNumber";
                case "fullName" -> "fullName";
                case "address" -> "address";
                case "gender" -> "gender";
                case "activeFlag" -> "activeFlag";
                case "createdDate" -> "createdDate";
                case "lastModifiedDate" -> "lastModifiedDate";
                default -> "id";
            };
        }
    },
}
