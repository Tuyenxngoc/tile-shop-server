package com.example.tileshop.specification;

import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.dto.filter.OrderFilterRequestDTO;
import com.example.tileshop.entity.*;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

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

    public static Specification<Order> filterByConditions(OrderFilterRequestDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get(Order_.STATUS), filter.getStatus()));
            }

            if (filter.getPaymentMethod() != null) {
                predicates.add(cb.equal(root.get(Order_.PAYMENT_METHOD), filter.getPaymentMethod()));
            }

            if (filter.getMinTotalAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Order_.TOTAL_AMOUNT), filter.getMinTotalAmount()));
            }

            if (filter.getMaxTotalAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Order_.TOTAL_AMOUNT), filter.getMaxTotalAmount()));
            }

            if (filter.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Order_.CREATED_DATE), filter.getFromDate()));
            }

            if (filter.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Order_.CREATED_DATE), filter.getToDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Order> filterByField(String searchBy, String keyword) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Order_.ID -> predicate = builder.and(predicate, builder.equal(root.get(Order_.id),
                            SpecificationsUtil.castToRequiredType(root.get(Order_.id).getJavaType(), keyword)));

                    case "fullName" -> {
                        Join<Order, User> user = root.join(Order_.USER, JoinType.LEFT);
                        predicate = builder.and(predicate, builder.like(
                                builder.lower(user.get(User_.FULL_NAME)),
                                "%" + keyword.toLowerCase() + "%"
                        ));
                    }

                    case "productName" -> {
                        query.distinct(true);
                        Join<Order, OrderItem> orderItems = root.join(Order_.ORDER_ITEMS, JoinType.LEFT);
                        Join<OrderItem, Product> product = orderItems.join(OrderItem_.PRODUCT, JoinType.LEFT);
                        predicate = builder.and(predicate, builder.like(
                                builder.lower(product.get(Product_.NAME)),
                                "%" + keyword.toLowerCase() + "%"
                        ));
                    }

                }
            }

            return predicate;
        };
    }
}
