package com.example.tileshop.service.impl;

import com.example.tileshop.dto.filter.TimeFilter;
import com.example.tileshop.dto.statistics.*;
import com.example.tileshop.repository.OrderRepository;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.repository.UserRepository;
import com.example.tileshop.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    @Override
    public DashboardStatsResponseDTO getStatistics(TimeFilter timeFilter) {
        LocalDate startDate = timeFilter.getStartDate();
        LocalDate endDate = timeFilter.getEndDate();
        LocalDate prevStartDate = startDate.minusMonths(1).withDayOfMonth(1);
        LocalDate prevEndDate = startDate.minusMonths(1).withDayOfMonth(startDate.minusMonths(1).lengthOfMonth());
        DashboardStatsResponseDTO dto = new DashboardStatsResponseDTO();

        // Revenue Stat
        double totalRevenue = orderRepository.getTotalRevenue(startDate, endDate);
        double revenueChange = orderRepository.getRevenueChangePercentage(startDate, endDate, prevStartDate, prevEndDate);

        RevenueStatDTO revenueStat = new RevenueStatDTO();
        revenueStat.setTotalRevenue(totalRevenue);
        revenueStat.setPercentageChange(revenueChange);
        revenueStat.setCurrency("VND");
        revenueStat.setPeriod(
                timeFilter.getStartDate() + " - " + timeFilter.getEndDate()
        );
        dto.setRevenueStat(revenueStat);

        // Order Stat
        int totalOrders = orderRepository.countOrders(startDate, endDate);
        int completedOrders = orderRepository.countCompletedOrders(startDate, endDate);
        int pendingOrders = orderRepository.countPendingOrders(startDate, endDate);
        int cancelledOrders = orderRepository.countCancelledOrders(startDate, endDate);
        double orderChange = orderRepository.getOrderChangePercentage(startDate, endDate, prevStartDate, prevEndDate);

        OrderStatDTO orderStat = new OrderStatDTO();
        orderStat.setTotalOrders(totalOrders);
        orderStat.setCompletedOrders(completedOrders);
        orderStat.setPendingOrders(pendingOrders);
        orderStat.setCancelledOrders(cancelledOrders);
        orderStat.setPercentageChange(orderChange);
        dto.setOrderStat(orderStat);

        // Customer Stat
        int totalCustomers = userRepository.countCustomers();
        int newCustomers = userRepository.countNewCustomers(startDate, endDate);
        double customerChange = userRepository.getCustomerChangePercentage(startDate, endDate, prevStartDate, prevEndDate);

        CustomerStatDTO customerStat = new CustomerStatDTO();
        customerStat.setTotalCustomers(totalCustomers);
        customerStat.setNewCustomers(newCustomers);
        customerStat.setPercentageChange(customerChange);
        dto.setCustomerStat(customerStat);

        // Product Stat
        int totalProducts = productRepository.countProducts();
        int outOfStock = productRepository.countOutOfStockProducts();
        int lowStock = productRepository.countLowStockProducts(10);
        double productChange = productRepository.getProductChangePercentage(startDate, endDate, prevStartDate, prevEndDate);

        ProductStatDTO productStat = new ProductStatDTO();
        productStat.setTotalProducts(totalProducts);
        productStat.setOutOfStockProducts(outOfStock);
        productStat.setLowStockProducts(lowStock);
        productStat.setPercentageChange(productChange);
        dto.setProductStat(productStat);

        return dto;
    }
}
