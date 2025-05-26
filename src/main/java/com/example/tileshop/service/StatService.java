package com.example.tileshop.service;

import com.example.tileshop.dto.filter.RevenueStatsFilter;
import com.example.tileshop.dto.filter.TimeFilter;
import com.example.tileshop.dto.statistics.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatService {
    DashboardStatsResponseDTO getStatistics(LocalDate date);

    List<TopSellingProductDTO> getTopSellingProducts(TimeFilter filter);

    List<TopCustomerDTO> getTopCustomers(TimeFilter filter);

    List<RecentOrderDTO> getRecentOrders();

    List<PointDTO> getRevenueStats(RevenueStatsFilter filter);

    List<CategoryRevenueChartDTO> getRevenueByCategory(RevenueStatsFilter filter);

    Map<String, MetricDTO> getChartData(ChartDataFilter filter);
}
