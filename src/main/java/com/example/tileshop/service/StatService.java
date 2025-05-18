package com.example.tileshop.service;

import com.example.tileshop.dto.filter.TimeFilter;
import com.example.tileshop.dto.order.OrderResponseDTO;
import com.example.tileshop.dto.product.ProductResponseDTO;
import com.example.tileshop.dto.statistics.DashboardStatsResponseDTO;
import com.example.tileshop.dto.user.UserResponseDTO;

import java.util.List;

public interface StatService {
    DashboardStatsResponseDTO getStatistics(TimeFilter timeFilter);

    List<ProductResponseDTO> getTopSellingProducts(TimeFilter filter);

    List<UserResponseDTO> getTopCustomers(TimeFilter filter);

    List<OrderResponseDTO> getRecentOrders();
}
