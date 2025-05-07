package com.example.tileshop.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatDTO {
    private int totalProducts;
    private int outOfStockProducts;
    private int lowStockProducts;
    private double percentageChange;
}