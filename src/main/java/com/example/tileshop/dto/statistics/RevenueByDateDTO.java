package com.example.tileshop.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueByDateDTO {
    private String date;      // "2025-05-01"

    private double revenue;   // 2000000
}
