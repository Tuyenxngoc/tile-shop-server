package com.example.tileshop.specification;

import com.example.tileshop.dto.filter.ProductFilterDTO;
import com.example.tileshop.entity.*;
import com.example.tileshop.util.SpecificationsUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> filterByField(String searchBy, String keyword) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case "id" -> predicate = builder.and(predicate, builder.equal(root.get(Product_.id),
                            SpecificationsUtil.castToRequiredType(root.get(Product_.id).getJavaType(), keyword)));

                    case "name" -> predicate = builder.and(predicate,
                            builder.like(builder.lower(root.get(Product_.name)), "%" + keyword.toLowerCase() + "%")
                    );

                    case "slug" -> predicate = builder.and(predicate,
                            builder.like(builder.lower(root.get(Product_.slug)), "%" + keyword.toLowerCase() + "%")
                    );

                    case "price" -> predicate = builder.and(predicate,
                            builder.equal(root.get(Product_.price),
                                    SpecificationsUtil.castToRequiredType(root.get(Product_.price).getJavaType(), keyword))
                    );

                    case "stockQuantity" -> predicate = builder.and(predicate,
                            builder.equal(root.get(Product_.stockQuantity),
                                    SpecificationsUtil.castToRequiredType(root.get(Product_.stockQuantity).getJavaType(), keyword))
                    );

                    case "discountPercentage" -> predicate = builder.and(predicate,
                            builder.equal(root.get(Product_.discountPercentage),
                                    SpecificationsUtil.castToRequiredType(root.get(Product_.discountPercentage).getJavaType(), keyword))
                    );

                    case "averageRating" -> predicate = builder.and(predicate,
                            builder.equal(root.get(Product_.averageRating),
                                    SpecificationsUtil.castToRequiredType(root.get(Product_.averageRating).getJavaType(), keyword))
                    );

                    case "categoryName" -> {
                        Join<Product, Category> categoryJoin = root.join(Product_.category);
                        predicate = builder.and(predicate,
                                builder.like(builder.lower(categoryJoin.get(Category_.name)),
                                        "%" + keyword.toLowerCase() + "%")
                        );
                    }

                    case "categorySlug" -> {
                        Join<Product, Category> categoryJoin = root.join(Product_.category);
                        predicate = builder.and(predicate,
                                builder.like(builder.lower(categoryJoin.get(Category_.slug)),
                                        "%" + keyword.toLowerCase() + "%")
                        );
                    }

                    case "brandName" -> {
                        Join<Product, Brand> brandJoin = root.join(Product_.brand, JoinType.LEFT);
                        predicate = builder.and(predicate,
                                builder.like(builder.lower(brandJoin.get(Brand_.name)),
                                        "%" + keyword.toLowerCase() + "%")
                        );
                    }

                    case "brandSlug" -> {
                        Join<Product, Brand> brandJoin = root.join(Product_.brand, JoinType.LEFT);
                        predicate = builder.and(predicate,
                                builder.like(builder.lower(brandJoin.get(Brand_.slug)),
                                        "%" + keyword.toLowerCase() + "%")
                        );
                    }
                }
            }

            return predicate;
        };
    }

    public static Specification<Product> filterByProductFilterDTO(ProductFilterDTO filterDTO) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterDTO == null) {
                return builder.conjunction();
            }

            // Lọc theo khoảng giá
            if (filterDTO.getMinPrice() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get(Product_.price), filterDTO.getMinPrice()));
            }
            if (filterDTO.getMaxPrice() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get(Product_.price), filterDTO.getMaxPrice()));
            }

            // Loại bỏ sản phẩm có ID cụ thể
            if (filterDTO.getExcludeId() != null) {
                predicates.add(builder.notEqual(root.get(Product_.id), filterDTO.getExcludeId()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> searchAllFields(String keyword) {
        return (root, query, builder) -> {
            if (StringUtils.isBlank(keyword)) {
                return builder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            // Text fields — sử dụng like
            predicates.add(builder.like(builder.lower(root.get(Product_.name)), "%" + keyword.toLowerCase() + "%"));
            predicates.add(builder.like(builder.lower(root.get(Product_.slug)), "%" + keyword.toLowerCase() + "%"));
            predicates.add(builder.like(builder.lower(root.get(Product_.description)), "%" + keyword.toLowerCase() + "%"));

            Join<Product, Category> categoryJoin = root.join(Product_.category);
            predicates.add(builder.like(builder.lower(categoryJoin.get(Category_.name)), "%" + keyword.toLowerCase() + "%"));

            Join<Product, Brand> brandJoin = root.join(Product_.brand, JoinType.LEFT);
            predicates.add(builder.like(builder.lower(brandJoin.get(Brand_.name)), "%" + keyword.toLowerCase() + "%"));

            // Numeric fields — nếu keyword là số hợp lệ mới thêm điều kiện
            if (StringUtils.isNumeric(keyword)) {
                predicates.add(builder.equal(root.get(Product_.id),
                        SpecificationsUtil.castToRequiredType(root.get(Product_.id).getJavaType(), keyword)));
                predicates.add(builder.equal(root.get(Product_.price),
                        SpecificationsUtil.castToRequiredType(root.get(Product_.price).getJavaType(), keyword)));
                predicates.add(builder.equal(root.get(Product_.stockQuantity),
                        SpecificationsUtil.castToRequiredType(root.get(Product_.stockQuantity).getJavaType(), keyword)));
                predicates.add(builder.equal(root.get(Product_.discountPercentage),
                        SpecificationsUtil.castToRequiredType(root.get(Product_.discountPercentage).getJavaType(), keyword)));
                predicates.add(builder.equal(root.get(Product_.averageRating),
                        SpecificationsUtil.castToRequiredType(root.get(Product_.averageRating).getJavaType(), keyword)));
            }

            return builder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
