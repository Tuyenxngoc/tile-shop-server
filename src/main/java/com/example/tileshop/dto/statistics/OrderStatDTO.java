package com.example.tileshop.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatDTO {
    private int totalOrders;

    private double percentageChange;

    private int completedOrders;

    private int pendingOrders;

    private int cancelledOrders;
}