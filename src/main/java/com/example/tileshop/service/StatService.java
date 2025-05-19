package com.example.tileshop.service;

import com.example.tileshop.dto.filter.TimeFilter;
import com.example.tileshop.dto.statistics.DashboardStatsResponseDTO;
import com.example.tileshop.dto.statistics.RecentOrderDTO;
import com.example.tileshop.dto.statistics.TopCustomerDTO;
import com.example.tileshop.dto.statistics.TopSellingProductDTO;

import java.util.List;

public interface StatService {
    DashboardStatsResponseDTO getStatistics(TimeFilter timeFilter);

    List<TopSellingProductDTO> getTopSellingProducts(TimeFilter filter);

    List<TopCustomerDTO> getTopCustomers(TimeFilter filter);

    List<RecentOrderDTO> getRecentOrders();
}
