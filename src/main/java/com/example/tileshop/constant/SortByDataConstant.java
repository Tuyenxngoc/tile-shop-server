package com.example.tileshop.constant;

public enum SortByDataConstant implements SortByInterface {

    USER {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "username" -> "username";
                case "fullName" -> "fullName";
                case "email" -> "email";
                case "phoneNumber" -> "phoneNumber";
                case "status" -> "status";
                case "address" -> "address";
                default -> "id";
            };
        }
    },

}
