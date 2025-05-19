package com.example.tileshop.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerDTO {
    private String id;

    private String fullName;

    private String email;

    private int totalOrders;

    private double totalSpent;

    private LocalDateTime lastOrderDate;
}
