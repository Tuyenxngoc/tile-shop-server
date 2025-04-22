package com.example.tileshop.specification;

import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecification {

    public static Specification<Order> hasUserId(String userId) {
        return (root, query, cb) -> cb.equal(root.get(Order_.USER).get(User_.ID), userId);
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get(Order_.STATUS), status);
    }

    public static Specification<Order> containsKeyword(String keyword) {
        return (root, query, cb) -> {
            query.distinct(true);

            Join<Order, OrderItem> orderItems = root.join(Order_.ORDER_ITEMS, JoinType.LEFT);
            Join<OrderItem, Product> product = orderItems.join(OrderItem_.PRODUCT, JoinType.LEFT);

            Predicate byProductName = cb.like(cb.lower(product.get(Product_.NAME)), "%" + keyword.toLowerCase() + "%");
            Predicate byOrderId = cb.like(cb.lower(root.get(Order_.ID).as(String.class)), "%" + keyword.toLowerCase() + "%");

            return cb.or(byProductName, byOrderId);
        };
    }
}
