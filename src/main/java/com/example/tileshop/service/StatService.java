package com.example.tileshop.service;

import com.example.tileshop.dto.filter.TimeFilter;
import com.example.tileshop.dto.statistics.*;

import java.util.List;

public interface StatService {
    DashboardStatsResponseDTO getStatistics(TimeFilter timeFilter);

    List<TopSellingProductDTO> getTopSellingProducts(TimeFilter filter);

    List<TopCustomerDTO> getTopCustomers(TimeFilter filter);

    List<RecentOrderDTO> getRecentOrders();

    List<RevenueByDateDTO> getRevenueStats(TimeFilter timeFilter);
}
