package com.example.tileshop.entity;

import com.example.tileshop.entity.common.DateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "news",
        indexes = {
                @Index(name = "IDX_NEWS_TITLE", columnList = "title"),
                @Index(name = "IDX_NEWS_SLUG", columnList = "slug")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "UN_NEWS_SLUG", columnNames = "slug")
        }
)
public class News extends DateAuditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false, length = 512)
    private String description;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(length = 512)
    private String imageUrl;

    @Column(nullable = false)
    private long viewCount;

    @ManyToOne
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_NEWS_CATEGORY_ID"), nullable = false)
    @JsonIgnore
    private NewsCategory category;
}
