package com.example.tileshop.dto.filter;

import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.constant.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilterRequestDTO {
    private OrderStatus status;// Trạng thái đơn

    private PaymentMethod paymentMethod;// Phương thức thanh toán

    private Double minTotalAmount;// Tổng tiền từ

    private Double maxTotalAmount;// Tổng tiền đến

    private LocalDateTime fromDate;// Ngày tạo từ

    private LocalDateTime toDate;// Ngày tạo đến
}
