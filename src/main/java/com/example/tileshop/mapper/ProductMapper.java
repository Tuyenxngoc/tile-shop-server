package com.example.tileshop.mapper;

import com.example.tileshop.dto.product.ProductResponseDTO;
import com.example.tileshop.dto.product.ProductUpdateResponseDTO;
import com.example.tileshop.dto.productattribute.ProductAttributeRequestDTO;
import com.example.tileshop.entity.Product;
import com.example.tileshop.entity.ProductImage;

public class ProductMapper {
    public static ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO dto = new ProductResponseDTO();
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
}