package com.example.tileshop.specification;

import com.example.tileshop.entity.Attribute;
import com.example.tileshop.entity.Attribute_;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class AttributeSpecification {
    public static Specification<Attribute> filterByField(String searchBy, String keyword) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case "id" -> predicate = builder.and(predicate, builder.equal(root.get(Attribute_.id),
                            SpecificationsUtil.castToRequiredType(root.get(Attribute_.id).getJavaType(), keyword)));

                    case "name" -> predicate = builder.and(predicate, builder.like(
                            builder.lower(root.get(Attribute_.name)),
                            "%" + keyword.toLowerCase() + "%"
                    ));

                    case "isRequired" -> {
                        String lowerKeyword = keyword.toLowerCase();
                        if ("true".equals(lowerKeyword) || "false".equals(lowerKeyword)) {
                            boolean boolValue = Boolean.parseBoolean(lowerKeyword);
                            predicate = builder.and(predicate, builder.equal(root.get("isRequired"), boolValue));
                        }
                    }

                    case "defaultValue" -> predicate = builder.and(predicate, builder.like(
                            builder.lower(root.get(Attribute_.defaultValue)),
                            "%" + keyword.toLowerCase() + "%"
                    ));
                }
            }

            return predicate;
        };
    }
}
