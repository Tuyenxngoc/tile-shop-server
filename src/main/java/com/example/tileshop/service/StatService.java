package com.example.tileshop.service;

import com.example.tileshop.dto.filter.RevenueStatsFilter;
import com.example.tileshop.dto.filter.TimeFilter;
import com.example.tileshop.dto.statistics.*;

import java.time.LocalDate;
import java.util.List;

public interface StatService {
    DashboardStatsResponseDTO getStatistics(LocalDate date);

    List<TopSellingProductDTO> getTopSellingProducts(TimeFilter filter);

    List<TopCustomerDTO> getTopCustomers(TimeFilter filter);

    List<RecentOrderDTO> getRecentOrders();

    List<Point> getRevenueStats(RevenueStatsFilter filter);
}
