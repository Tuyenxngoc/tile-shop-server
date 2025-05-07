package com.example.tileshop.service;

import com.example.tileshop.dto.filter.TimeFilter;
import com.example.tileshop.dto.statistics.DashboardStatsResponseDTO;

public interface StatService {
    DashboardStatsResponseDTO getStatistics(TimeFilter timeFilter);
}
