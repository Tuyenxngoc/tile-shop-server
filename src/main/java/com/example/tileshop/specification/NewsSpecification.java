package com.example.tileshop.specification;

import com.example.tileshop.entity.News;
import com.example.tileshop.entity.NewsCategory;
import com.example.tileshop.entity.NewsCategory_;
import com.example.tileshop.entity.News_;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class NewsSpecification {

    public static Specification<News> excludeSpecificId(Long excludeId) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (excludeId != null) {
                predicate = builder.notEqual(root.get(News_.id), excludeId);
            }

            return predicate;
        };
    }

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
                        Join<News, NewsCategory> categoryJoin = root.join(News_.category);
                        predicate = builder.like(
                                builder.lower(categoryJoin.get(NewsCategory_.name)),
                                "%" + keyword.toLowerCase() + "%"
                        );
                    }

                    case "categoryId" -> {
                        Join<News, NewsCategory> categoryJoin = root.join(News_.category);
                        predicate = builder.and(predicate, builder.equal(
                                categoryJoin.get(NewsCategory_.id),
                                SpecificationsUtil.castToRequiredType(categoryJoin.get(NewsCategory_.id).getJavaType(), keyword)
                        ));
                    }
                }
            }

            return predicate;
        };
    }
}
