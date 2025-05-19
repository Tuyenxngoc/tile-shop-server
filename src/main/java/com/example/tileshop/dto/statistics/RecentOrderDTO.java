package com.example.tileshop.dto.statistics;

import com.example.tileshop.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderDTO {
    private long id;

    private double totalAmount;

    private OrderStatus status;

    private LocalDateTime createdDate;

    private String fullName;
}
