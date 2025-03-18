package com.example.tileshop.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    private int quantity;

    private double priceAtTimeOfOrder;

    @ManyToOne
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "FK_ORDER_ITEM_ORDER_ID"), nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "FK_ORDER_ITEM_PRODUCT_ID"), nullable = false)
    @JsonIgnore
    private Product product;

}
