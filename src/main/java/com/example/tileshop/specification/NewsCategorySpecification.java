package com.example.tileshop.specification;

import com.example.tileshop.entity.NewsCategory;
import com.example.tileshop.entity.NewsCategory_;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class NewsCategorySpecification {
    public static Specification<NewsCategory> filterByField(String searchBy, String keyword) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case NewsCategory_.ID ->
                            predicate = builder.and(predicate, builder.equal(root.get(NewsCategory_.id),
                                    SpecificationsUtil.castToRequiredType(root.get(NewsCategory_.id).getJavaType(), keyword)));

                    case NewsCategory_.NAME -> predicate = builder.and(predicate, builder.like(
                            builder.lower(root.get(NewsCategory_.name)),
                            "%" + keyword.toLowerCase() + "%"
                    ));
                }
            }

            return predicate;
        };
    }
}
