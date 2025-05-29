package com.example.tileshop.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatDTO {
    private long totalCustomers;

    private double percentageChange;

    private long newCustomers;
}