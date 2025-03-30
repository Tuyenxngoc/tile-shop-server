package com.example.tileshop.specification;

import com.example.tileshop.entity.Brand;
import com.example.tileshop.entity.Brand_;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class BrandSpecification {
    public static Specification<Brand> filterByField(String searchBy, String keyword) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {

                    case Brand_.ID -> predicate = builder.and(predicate, builder.equal(root.get(Brand_.id),
                            SpecificationsUtil.castToRequiredType(root.get(Brand_.id).getJavaType(), keyword)));

                    case Brand_.NAME -> predicate = builder.and(predicate, builder.like(
                            builder.lower(root.get(Brand_.name)),
                            "%" + keyword.toLowerCase() + "%"
                    ));
                }
            }

            return predicate;
        };
    }
}
