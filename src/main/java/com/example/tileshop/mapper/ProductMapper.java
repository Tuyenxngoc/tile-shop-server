package com.example.tileshop.mapper;

import com.example.tileshop.dto.category.CategorySimpleDTO;
import com.example.tileshop.dto.product.ProductDetailResponseDTO;
import com.example.tileshop.dto.product.ProductResponseDTO;
import com.example.tileshop.dto.product.ProductUpdateResponseDTO;
import com.example.tileshop.dto.productattribute.ProductAttributeRequestDTO;
import com.example.tileshop.dto.productattribute.ProductAttributeResponseDTO;
import com.example.tileshop.entity.Category;
import com.example.tileshop.entity.Product;
import com.example.tileshop.entity.ProductImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductMapper {
    private static List<CategorySimpleDTO> buildBreadcrumb(Category category) {
        List<CategorySimpleDTO> breadcrumb = new ArrayList<>();
        Category current = category;

        while (current != null) {
            breadcrumb.add(new CategorySimpleDTO(current.getId(), current.getName(), current.getSlug()));
            current = current.getParent();
        }

        Collections.reverse(breadcrumb);
        return breadcrumb;
    }

    public static ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setCreatedDate(product.getCreatedDate());
        dto.setLastModifiedDate(product.getLastModifiedDate());
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setSalePrice(product.calculateFinalPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setAverageRating(product.getAverageRating());
        dto.setImageUrl(
                product.getImages() != null && !product.getImages().isEmpty()
                        ? product.getImages().getFirst().getImageUrl()
                        : null
        );
        dto.setCategory(CategoryMapper.toSimpleDTO(product.getCategory()));

        return dto;
    }

    public static ProductUpdateResponseDTO toUpdateResponseDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductUpdateResponseDTO dto = new ProductUpdateResponseDTO();
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategoryId(product.getCategory().getId());
        dto.setBrandId(product.getBrand() == null ? null : product.getBrand().getId());
        dto.setAttributes(product.getAttributes().stream()
                .map(attr -> {
                            ProductAttributeRequestDTO a = new ProductAttributeRequestDTO();
                            a.setAttributeId(attr.getAttribute().getId());
                            a.setValue(attr.getValue());
                            return a;
                        }
                )
                .toList());
        dto.setImages(product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList());

        return dto;
    }

    public static ProductDetailResponseDTO toDetailResponseDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDetailResponseDTO dto = new ProductDetailResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setSalePrice(product.calculateFinalPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setAverageRating(product.getAverageRating());

        dto.setCategoryPath(buildBreadcrumb(product.getCategory()));

        if (product.getBrand() != null) {
            dto.setBrand(BrandMapper.toDTO(product.getBrand()));
        }

        dto.setImages(
                product.getImages().stream()
                        .map(ProductImage::getImageUrl)
                        .toList()
        );

        List<ProductAttributeResponseDTO> attributes = product.getAttributes().stream()
                .map(ProductAttributeMapper::toDTO)
                .toList();
        dto.setAttributes(attributes);

        return dto;
    }
}