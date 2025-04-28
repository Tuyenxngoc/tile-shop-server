package com.example.tileshop.specification;

import com.example.tileshop.entity.Category;
import com.example.tileshop.entity.Category_;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {
    public static Specification<Category> filterByField(String searchBy, String keyword) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Category_.ID -> predicate = builder.and(predicate, builder.equal(root.get(Category_.id),
                            SpecificationsUtil.castToRequiredType(root.get(Category_.id).getJavaType(), keyword)));

                    case Category_.NAME -> predicate = builder.and(predicate, builder.like(
                            builder.lower(root.get(Category_.name)),
                            "%" + keyword.toLowerCase() + "%"
                    ));
                }
            }

            return predicate;
        };
    }
}
