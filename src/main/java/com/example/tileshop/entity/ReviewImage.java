package com.example.tileshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "review_images")
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long id;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "review_id", foreignKey = @ForeignKey(name = "FK_REVIEW_IMAGE_REVIEW_ID"), nullable = false)
    @JsonIgnore
    private Review review;

}
