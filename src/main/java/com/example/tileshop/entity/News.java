package com.example.tileshop.entity;

import com.example.tileshop.entity.common.FlagDateAuditing;
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
public class News extends FlagDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 500)
    private String slug;

    @Column(nullable = false, length = 1500)
    private String description;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_NEWS_CATEGORY_ID"), nullable = false)
    @JsonIgnore
    private NewsCategory category;

}
