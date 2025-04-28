package com.example.tileshop.specification;

import com.example.tileshop.constant.ReviewStatus;
import com.example.tileshop.dto.filter.ReviewFilterDTO;
import com.example.tileshop.entity.*;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ReviewSpecification {
    public static Specification<Review> filterByProductId(Long productId) {
        return (root, query, builder) -> {
            if (productId == null) {
                return builder.conjunction();
            }

            Join<Review, Product> productJoin = root.join(Review_.product);
            return builder.equal(productJoin.get(Product_.id), productId);
        };
    }

    public static Specification<Review> filterByStatus(ReviewStatus status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }

            return builder.equal(root.get(Review_.status), status);
        };
    }

    public static Specification<Review> filterByField(String searchBy, String keyword) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Review_.RATING -> predicate = builder.and(predicate, builder.equal(root.get(Review_.rating),
                            SpecificationsUtil.castToRequiredType(root.get(Review_.rating).getJavaType(), keyword)));

                    case Review_.COMMENT -> predicate = builder.and(predicate, builder.like(
                            builder.lower(root.get(Review_.comment)),
                            "%" + keyword.toLowerCase() + "%"
                    ));

                    case Product_.ID -> {
                        Join<Review, Product> productJoin = root.join(Review_.product);
                        predicate = builder.and(predicate,
                                builder.equal(
                                        productJoin.get(Product_.id),
                                        SpecificationsUtil.castToRequiredType(productJoin.get(Product_.id).getJavaType(), keyword)
                                )
                        );
                    }

                    case Product_.NAME -> {
                        Join<Review, Product> productJoin = root.join(Review_.product);
                        predicate = builder.and(predicate,
                                builder.like(
                                        builder.lower(productJoin.get(Product_.name)),
                                        "%" + keyword.toLowerCase() + "%"
                                )
                        );
                    }

                    case Product_.CATEGORY -> {
                        Join<Review, Product> productJoin = root.join(Review_.product);
                        Join<Product, Category> categoryJoin = productJoin.join(Product_.category);
                        predicate = builder.and(predicate,
                                builder.like(
                                        builder.lower(categoryJoin.get(Category_.name)),
                                        "%" + keyword.toLowerCase() + "%"
                                )
                        );
                    }
                }
            }

            return predicate;
        };
    }

    public static Specification<Review> filterByReviewFilterDTO(ReviewFilterDTO filterDTO) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (filterDTO == null) {
                return predicate;
            }

            // Lọc theo rating (số sao)
            if (filterDTO.getRating() != null) {
                predicate = builder.and(predicate, builder.equal(root.get(Review_.rating), filterDTO.getRating()));
            }

            // Lọc review có hình ảnh
            if (Boolean.TRUE.equals(filterDTO.getHasImage())) {
                predicate = builder.and(predicate, builder.isNotEmpty(root.get(Review_.images)));
            }

            // Lọc review có nội dung
            if (Boolean.TRUE.equals(filterDTO.getHasContent())) {
                predicate = builder.and(predicate,
                        builder.isNotNull(root.get(Review_.comment)),
                        builder.notEqual(builder.trim(root.get(Review_.comment)), "")
                );
            }

            return predicate;
        };
    }
}
