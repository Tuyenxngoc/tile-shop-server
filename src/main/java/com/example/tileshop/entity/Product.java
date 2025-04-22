package com.example.tileshop.entity;

import com.example.tileshop.entity.common.DateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "products",
        indexes = {
                @Index(name = "IDX_PRODUCT_NAME", columnList = "name"),
                @Index(name = "IDX_PRODUCT_SLUG", columnList = "slug")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "UN_PRODUCT_SLUG", columnNames = "slug")
        }
)
public class Product extends DateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String slug;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String description;

    private double price;

    private Double discountPercentage;

    private int stockQuantity;

    @Column(nullable = false)
    private double averageRating = 0.0;

    @ManyToOne
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_PRODUCT_CATEGORY_ID"), nullable = false)
    @JsonIgnore
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id", foreignKey = @ForeignKey(name = "FK_PRODUCT_BRAND_ID"))
    @JsonIgnore
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ProductAttribute> attributes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CartItem> cartItems = new ArrayList<>();

    public double calculateFinalPrice() {
        if (discountPercentage != null && discountPercentage > 0) {
            double discountAmount = (price * discountPercentage) / 100;
            return price - discountAmount;
        }
        return price;
    }
}
