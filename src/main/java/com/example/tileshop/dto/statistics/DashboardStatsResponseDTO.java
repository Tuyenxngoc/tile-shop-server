package com.example.tileshop.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponseDTO {
    private RevenueStatDTO revenueStat;

    private OrderStatDTO orderStat;

    private CustomerStatDTO customerStat;

    private ProductStatDTO productStat;
}
