package com.example.tileshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "product_attributes")
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_attribute_id")
    private Long id;

    private String value;

    @ManyToOne
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "FK_PRODUCT_ATTRIBUTE_PRODUCT_ID"), nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "attribute_id", foreignKey = @ForeignKey(name = "FK_PRODUCT_ATTRIBUTE_ATTRIBUTE_ID"), nullable = false)
    private Attribute attribute;

}
