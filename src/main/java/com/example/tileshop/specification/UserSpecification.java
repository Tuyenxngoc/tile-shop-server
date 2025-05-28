package com.example.tileshop.specification;

import com.example.tileshop.entity.Role;
import com.example.tileshop.entity.Role_;
import com.example.tileshop.entity.User;
import com.example.tileshop.entity.User_;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> filterByField(String searchBy, String keyword) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case "id" -> predicate = builder.and(predicate,
                            builder.equal(root.get(User_.id),
                                    SpecificationsUtil.castToRequiredType(root.get(User_.id).getJavaType(), keyword)));

                    case "username" -> predicate = builder.and(predicate,
                            builder.like(builder.lower(root.get(User_.username)), "%" + keyword.toLowerCase() + "%"));

                    case "email" -> predicate = builder.and(predicate,
                            builder.like(builder.lower(root.get(User_.email)), "%" + keyword.toLowerCase() + "%"));

                    case "fullName" -> predicate = builder.and(predicate,
                            builder.like(builder.lower(root.get(User_.fullName)), "%" + keyword.toLowerCase() + "%"));

                    case "phoneNumber" -> predicate = builder.and(predicate,
                            builder.like(builder.lower(root.get(User_.phoneNumber)), "%" + keyword.toLowerCase() + "%"));

                    case "roleName" -> {
                        Join<User, Role> roleJoin = root.join(User_.role);
                        predicate = builder.and(predicate, builder.like(
                                builder.lower(roleJoin.get(Role_.name)),
                                "%" + keyword.toLowerCase() + "%"
                        ));
                    }
                }
            }

            return predicate;
        };
    }
}
