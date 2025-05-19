package com.example.tileshop.dto.statistics;

import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.dto.user.UserResponseDTO;
import com.example.tileshop.entity.User;
import com.example.tileshop.mapper.UserMapper;
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

    private UserResponseDTO customer;

    public RecentOrderDTO(long id, double totalAmount, OrderStatus status, LocalDateTime createdDate, User user) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdDate = createdDate;
        this.customer = UserMapper.toDTO(user);
    }
}
