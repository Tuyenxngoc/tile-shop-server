package com.example.tileshop.domain.entity;

import com.example.tileshop.domain.entity.common.DateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "reviews")
public class Review extends DateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    private int rating;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "FK_REVIEW_CUSTOMER_ID"), nullable = false)
    @JsonIgnore
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "FK_REVIEW_PRODUCT_ID"), nullable = false)
    @JsonIgnore
    private Product product;

}
