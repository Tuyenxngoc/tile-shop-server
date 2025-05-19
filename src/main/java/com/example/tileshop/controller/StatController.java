package com.example.tileshop.controller;

import com.example.tileshop.annotation.RestApiV1;
import com.example.tileshop.constant.UrlConstant;
import com.example.tileshop.dto.filter.TimeFilter;
import com.example.tileshop.service.StatService;
import com.example.tileshop.util.VsResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Statistics")
public class StatController {
    StatService statService;

    @Operation(summary = "API Get Store Statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Stat.GET_DASHBOARD_STATS)
    public ResponseEntity<?> getDashboardStatistics(@ParameterObject TimeFilter timeFilter) {
        return VsResponseUtil.success(statService.getStatistics(timeFilter));
    }

    @Operation(summary = "API Get Top Best Selling Products")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Stat.GET_TOP_SELLING_PRODUCTS)
    public ResponseEntity<?> getTopSellingProducts(@ParameterObject TimeFilter timeFilter) {
        return VsResponseUtil.success(statService.getTopSellingProducts(timeFilter));
    }

    @Operation(summary = "API Get Top Customers With Most Purchases")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Stat.GET_TOP_CUSTOMERS)
    public ResponseEntity<?> getTopCustomers(@ParameterObject TimeFilter timeFilter) {
        return VsResponseUtil.success(statService.getTopCustomers(timeFilter));
    }

    @Operation(summary = "API Get Recent Orders")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Stat.GET_RECENT_ORDERS)
    public ResponseEntity<?> getRecentOrders() {
        return VsResponseUtil.success(statService.getRecentOrders());
    }

    @Operation(summary = "API Get Revenue Statistics By Time")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(UrlConstant.Stat.GET_REVENUE_STATS)
    public ResponseEntity<?> getRevenueStats(@ParameterObject TimeFilter timeFilter) {
        return VsResponseUtil.success(statService.getRevenueStats(timeFilter));
    }
}
