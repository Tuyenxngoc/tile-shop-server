package com.example.tileshop.specification;

import com.example.tileshop.entity.Category_;
import com.example.tileshop.entity.News;
import com.example.tileshop.entity.News_;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class NewsSpecification {
    public static Specification<News> filterByField(String searchBy, String keyword) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case News_.ID -> predicate = builder.and(predicate, builder.equal(root.get(News_.id),
                            SpecificationsUtil.castToRequiredType(root.get(News_.id).getJavaType(), keyword)));

                    case News_.TITLE -> predicate = builder.and(predicate, builder.like(
                            builder.lower(root.get(News_.title)),
                            "%" + keyword.toLowerCase() + "%"
                    ));

                    case News_.CATEGORY -> {
                        Join<?, ?> categoryJoin = root.join(News_.category);
                        predicate = builder.and(predicate, builder.equal(categoryJoin.get(Category_.ID),
                                SpecificationsUtil.castToRequiredType(categoryJoin.get(Category_.ID).getJavaType(), keyword)));
                    }
                }
            }

            return predicate;
        };
    }
}
