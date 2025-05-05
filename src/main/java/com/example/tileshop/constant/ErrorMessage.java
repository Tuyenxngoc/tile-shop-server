package com.example.tileshop.constant;

public class ErrorMessage {
    public static final String ERR_EXCEPTION_GENERAL = "exception.general";
    public static final String ERR_UNAUTHORIZED = "exception.unauthorized";
    public static final String ERR_FORBIDDEN = "exception.forbidden";
    public static final String ERR_FORBIDDEN_UPDATE_DELETE = "exception.forbidden.update-delete";
    public static final String ERR_METHOD_NOT_SUPPORTED = "exception.method-not-supported";
    public static final String ERR_REQUEST_BODY = "exception.required-request-body-missing";
    public static final String ERR_REQUIRED_MISSING_PARAMETER = "exception.missing-servlet-request-parameter";
    public static final String ERR_METHOD_ARGUMENT_TYPE_MISMATCH = "exception.method-argument-type-mismatch";
    public static final String ERR_RESOURCE_NOT_FOUND = "exception.resource-not-found";
    public static final String ERR_MISSING_SERVLET_REQUEST_PART = "exception.request.part.missing";
    public static final String ERR_UNSUPPORTED_MEDIA_TYPE = "exception.unsupported.media.type";
    public static final String ERR_MULTIPART_EXCEPTION = "exception.multipart";
    public static final String ERR_ILLEGAL_ARGUMENT = "exception.illegal-arguments";

    public static final String INVALID_SOME_THING_FIELD = "invalid.general";
    public static final String INVALID_FORMAT_SOME_THING_FIELD = "invalid.general.format";
    public static final String INVALID_NUMBER_FORMAT = "invalid.general.number-format";
    public static final String INVALID_SOME_THING_FIELD_IS_REQUIRED = "invalid.general.required";
    public static final String INVALID_ARRAY_IS_REQUIRED = "invalid.array.required";
    public static final String INVALID_ARRAY_NOT_EMPTY = "invalid.array.not.empty";
    public static final String INVALID_ARRAY_LENGTH = "invalid.array.length";
    public static final String INVALID_NOT_BLANK_FIELD = "invalid.general.not-blank";
    public static final String INVALID_NOT_NULL_FIELD = "invalid.general.not-null";
    public static final String INVALID_FORMAT_NAME = "invalid.name-format";
    public static final String INVALID_FORMAT_USERNAME = "invalid.username-format";
    public static final String INVALID_FORMAT_PASSWORD = "invalid.password-format";
    public static final String INVALID_REPEAT_PASSWORD = "invalid.password-repeat";
    public static final String INVALID_FORMAT_EMAIL = "invalid.email-format";
    public static final String INVALID_FORMAT_PHONE = "invalid.phone-format";
    public static final String INVALID_COORDINATES = "invalid.coordinates";
    public static final String INVALID_MINIMUM_ONE = "invalid.minimum-one";
    public static final String INVALID_MINIMUM_ZERO = "invalid.minimum-zero";
    public static final String INVALID_MAXIMUM_ONE_HUNDRED = "invalid.maximum-one-hundred";
    public static final String INVALID_MAXIMUM_FIVE = "invalid.maximum-five";
    public static final String INVALID_MAXIMUM_INT = "invalid.maximum-int";
    public static final String INVALID_MAXIMUM_SHORT = "invalid.maximum-short";
    public static final String INVALID_TEXT_LENGTH = "invalid.text.length";
    public static final String INVALID_KEYWORD_LENGTH = "invalid.keyword.length";

    //Date
    public static final String INVALID_DATE = "invalid.date-format";
    public static final String INVALID_DATE_FEATURE = "invalid.date-future";
    public static final String INVALID_DATE_PAST = "invalid.date-past";
    public static final String INVALID_TIME = "invalid.time-format";
    public static final String INVALID_LOCAL_DATE_FORMAT = "invalid.local-date-format";
    public static final String INVALID_LOCAL_DATE_TIME_FORMAT = "invalid.local-date-time-format";

    //File
    public static final String INVALID_MAX_UPLOAD_SIZE_EXCEEDED = "invalid.max-upload-size-exceeded";
    public static final String INVALID_FILE_REQUIRED = "invalid.file.required";
    public static final String INVALID_FILE_SIZE = "invalid.file.size";
    public static final String INVALID_FILE_TYPE = "invalid.file.type";
    public static final String INVALID_URL_FORMAT = "invalid.url-format";
    public static final String INVALID_OPERATOR_NOT_SUPPORTED = "invalid.operator.not-supported";

    public static class Auth {
        public static final String ERR_INCORRECT_USERNAME_PASSWORD = "exception.auth.incorrect.username-password";
        public static final String ERR_INCORRECT_PASSWORD = "exception.auth.incorrect.password";
        public static final String ERR_DUPLICATE_USERNAME = "exception.auth.duplicate.username";
        public static final String ERR_DUPLICATE_EMAIL = "exception.auth.duplicate.email";
        public static final String ERR_INVALID_REFRESH_TOKEN = "exception.auth.invalid.refresh.token";
        public static final String ERR_ACCOUNT_DISABLED = "exception.auth.account.disabled";
    }

    public static class Attribute {
        public static final String ERR_NOT_FOUND_ID = "exception.attribute.not.found.id";
        public static final String ERR_DUPLICATE_NAME = "exception.attribute.duplicate.name";
    }

    public static class Brand {
        public static final String ERR_NOT_FOUND_ID = "exception.brand.not.found.id";
        public static final String ERR_DUPLICATE_NAME = "exception.brand.duplicate.name";
        public static final String ERR_DUPLICATE_SLUG = "exception.brand.duplicate.slug";
        public static final String ERR_DELETE_HAS_PRODUCTS = "exception.brand.delete.has.products";
    }

    public static class Cart {
        public static final String ERR_MAX_ITEMS = "exception.cart.max-items";
        public static final String ERR_EMPTY_CART = "exception.cart.empty";
    }

    public static class CartItem {
        public static final String ERR_NOT_FOUND_ID = "exception.cart-item.not.found.id";
    }

    public static class Category {
        public static final String ERR_NOT_FOUND_ID = "exception.category.not.found.id";
        public static final String ERR_DUPLICATE_NAME = "exception.category.duplicate.name";
        public static final String ERR_DUPLICATE_SLUG = "exception.category.duplicate.slug";
        public static final String ERR_SELF_PARENT = "exception.category.self.parent";
        public static final String ERR_CHILD_AS_PARENT = "exception.category.child.as.parent";
        public static final String ERR_HAS_CHILDREN = "exception.category.has.children";
        public static final String ERR_HAS_PRODUCTS = "exception.category.has.products";
    }

    public static class CategoryAttribute {
    }

    public static class News {
        public static final String ERR_NOT_FOUND_ID = "exception.news.not.found.id";
        public static final String ERR_NOT_FOUND_SLUG = "exception.news.not.found.slug";
        public static final String ERR_DUPLICATE_SLUG = "exception.news.duplicate.slug";
    }

    public static class NewsCategory {
        public static final String ERR_NOT_FOUND_ID = "exception.news.category.not.found.id";
        public static final String ERR_DUPLICATE_NAME = "exception.news.category.duplicate.name";
        public static final String ERR_DUPLICATE_SLUG = "exception.news.category.duplicate.slug";
        public static final String ERR_HAS_NEWS = "exception.news.category.has.news";
    }

    public static class Order {
        public static final String ERR_NOT_FOUND_ID = "exception.order.not.found.id";
        public static final String ERR_MISSING_SHIPPING_ADDRESS = "exception.order.missing.shipping.address";
        public static final String ERR_MISSING_INVOICE_INFO = "exception.order.missing.invoice.info";
        public static final String ERR_ALREADY_PAID = "exception.order.already.paid";
        public static final String ERR_ORDER_CANCELLED = "exception.order.cancelled";
        public static final String ERR_INVALID_OR_TAMPERED_DATA = "exception.order.invalid.or.tampered.data";
        public static final String ERR_SAME_STATUS = "exception.order.same-status";
        public static final String ERR_INVALID_STATUS_TRANSITION = "exception.order.invalid-status-transition";
    }

    public static class OrderItem {
    }

    public static class Product {
        public static final String ERR_NOT_FOUND_ID = "exception.product.not.found.id";
        public static final String ERR_NOT_FOUND_SLUG = "exception.product.not.found.slug";
        public static final String ERR_OUT_OF_STOCK = "exception.product.out-of-stock";
        public static final String ERR_DUPLICATE_SLUG = "exception.product.duplicate.slug";
        public static final String ERR_EXIST_ORDER_ITEMS = "exception.product.exist.order-items";
        public static final String ERR_EXIST_CART_ITEMS = "exception.product.exist.cart-items";
    }

    public static class ProductAttribute {
    }

    public static class ProductImage {
    }

    public static class Review {
        public static final String ERR_NOT_FOUND_ID = "exception.review.not.found.id";
        public static final String ERR_MAX_IMAGES = "exception.review.max.images";
        public static final String ERR_PENDING_LIMIT = "exception.review.pending.limit";
    }

    public static class Role {
        public static final String ERR_NOT_FOUND_ID = "exception.role.not.found.id";
        public static final String ERR_NOT_FOUND_NAME = "exception.role.not.found.name";
    }

    public static class Slide {
        public static final String ERR_NOT_FOUND_ID = "exception.slide.not.found.id";
    }

    public static class User {
        public static final String ERR_NOT_FOUND_USERNAME = "exception.user.not.found.username";
        public static final String ERR_NOT_FOUND_EMAIL = "exception.user.not.found.email";
        public static final String ERR_NOT_FOUND_ID = "exception.user.not.found.id";
        public static final String ERR_NOT_FOUND_ACCOUNT = "exception.user.not.found.account";
        public static final String RATE_LIMIT = "exception.user.rate.limit";
        public static final String ERR_DELETE_HAS_ORDERS = "exception.user.delete.has.orders";
        public static final String ERR_DELETE_HAS_REVIEWS = "exception.user.delete.has.reviews";
    }
}
