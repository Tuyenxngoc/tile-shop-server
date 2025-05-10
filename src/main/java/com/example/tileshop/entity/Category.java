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
@Table(
        name = "categories",
        indexes = {
                @Index(name = "IDX_CATEGORY_PARENT_ID", columnList = "parent_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_CATEGORY_NAME", columnNames = "name"),
                @UniqueConstraint(name = "UK_CATEGORY_SLUG", columnNames = "slug")
        }
)
public class Category extends DateAuditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(length = 512)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "FK_CATEGORY_PARENT_ID"))
    @JsonIgnore
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Category> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<CategoryAttribute> categoryAttributes = new ArrayList<>();
}
