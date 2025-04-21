package com.example.tileshop.dto.product;

import com.example.tileshop.dto.brand.BrandResponseDTO;
import com.example.tileshop.dto.category.CategorySimpleResponseDTO;
import com.example.tileshop.dto.productattribute.ProductAttributeResponseDTO;
import com.example.tileshop.entity.Category;
import com.example.tileshop.entity.Product;
import com.example.tileshop.entity.ProductAttribute;
import com.example.tileshop.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProductDetailResponseDTO {
    private long id;

    private String name;

    private String slug;

    private String description;

    private double price;

    private Double discountPercentage;

    private double salePrice;

    private int stockQuantity;

    private double averageRating;

    private List<CategorySimpleResponseDTO> categoryPath;

    private BrandResponseDTO brand;

    private List<String> images;

    private List<ProductAttributeResponseDTO> attributes;

    public ProductDetailResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.slug = product.getSlug();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.discountPercentage = product.getDiscountPercentage();
        this.salePrice = product.calculateFinalPrice();
        this.stockQuantity = product.getStockQuantity();
        this.averageRating = product.getAverageRating();

        this.categoryPath = buildBreadcrumb(product.getCategory());

        if (product.getBrand() != null) {
            this.brand = new BrandResponseDTO(product.getBrand());
        }

        this.images = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();

        this.attributes = new ArrayList<>();
        for (ProductAttribute attribute : product.getAttributes()) {
            this.attributes.add(new ProductAttributeResponseDTO(attribute));
        }
    }

    public List<CategorySimpleResponseDTO> buildBreadcrumb(Category category) {
        List<CategorySimpleResponseDTO> breadcrumb = new ArrayList<>();
        Category current = category;

        while (current != null) {
            breadcrumb.add(new CategorySimpleResponseDTO(current.getId(), current.getName()));
            current = current.getParent();
        }

        Collections.reverse(breadcrumb);
        return breadcrumb;
    }

}
