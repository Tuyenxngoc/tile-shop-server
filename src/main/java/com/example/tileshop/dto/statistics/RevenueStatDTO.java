package com.example.tileshop.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatDTO {
    private double totalRevenue;
    private double percentageChange;
    private String currency;
    private String period; // VD: "Tháng này" hoặc "01/05/2025 - 07/05/2025"
}