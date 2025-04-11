package com.example.tileshop.specification;

import com.example.tileshop.constant.ReviewStatus;
import com.example.tileshop.entity.Product;
import com.example.tileshop.entity.Product_;
import com.example.tileshop.entity.Review;
import com.example.tileshop.entity.Review_;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

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
                }
            }

            return predicate;
        };
    }

}
