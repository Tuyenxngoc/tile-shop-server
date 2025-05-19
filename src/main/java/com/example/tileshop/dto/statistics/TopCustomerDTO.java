package com.example.tileshop.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerDTO {
    private String id;

    private String username;

    private String fullName;

    private String email;

    private long totalOrders;

    private double totalSpent;
}
