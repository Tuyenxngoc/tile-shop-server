package com.example.tileshop.service.impl;

import com.example.tileshop.dto.filter.TimeFilter;
import com.example.tileshop.dto.statistics.*;
import com.example.tileshop.repository.OrderRepository;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.repository.UserRepository;
import com.example.tileshop.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private double getProductChangePercentage(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime prevStartDate, LocalDateTime prevEndDate) {
        int currentCount = productRepository.countProductsCreatedBetween(startDate, endDate);
        int previousCount = productRepository.countProductsCreatedBetween(prevStartDate, prevEndDate);

        if (previousCount == 0) {
            return currentCount > 0 ? 100.0 : 0.0;
        }

        return ((double) (currentCount - previousCount) / previousCount) * 100;
    }

    private double getOrderChangePercentage(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime prevStartDate, LocalDateTime prevEndDate) {
        int currentCount = orderRepository.countOrders(startDate, endDate);
        int previousCount = orderRepository.countOrders(prevStartDate, prevEndDate);
        if (previousCount == 0) return 100.0;
        return ((double) (currentCount - previousCount) / previousCount) * 100;
    }

    private double getRevenueChangePercentage(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime prevStartDate, LocalDateTime prevEndDate) {
        double currentRevenue = orderRepository.getTotalRevenue(startDate, endDate);
        double previousRevenue = orderRepository.getTotalRevenue(prevStartDate, prevEndDate);
        if (previousRevenue == 0) return 100.0;
        return ((currentRevenue - previousRevenue) / previousRevenue) * 100;
    }

    @Override
    public DashboardStatsResponseDTO getStatistics(TimeFilter timeFilter) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1); // hôm qua
        LocalDateTime endDate = LocalDateTime.now();                // hôm nay
        LocalDateTime prevStartDate = startDate;
        LocalDateTime prevEndDate = startDate;

        DashboardStatsResponseDTO responseDTO = new DashboardStatsResponseDTO();

        // Revenue Stat
        double totalRevenue = orderRepository.getTotalRevenue(startDate, endDate);
        double revenueChange = getRevenueChangePercentage(startDate, endDate, prevStartDate, prevEndDate);

        RevenueStatDTO revenueStat = new RevenueStatDTO();
        revenueStat.setTotalRevenue(totalRevenue);
        revenueStat.setPercentageChange(revenueChange);
        revenueStat.setCurrency("VND");
        revenueStat.setPeriod(
                timeFilter.getStartDate() + " - " + timeFilter.getEndDate()
        );
        responseDTO.setRevenueStat(revenueStat);

        // Order Stat
        int totalOrders = orderRepository.countOrders(startDate, endDate);
        int completedOrders = orderRepository.countCompletedOrders(startDate, endDate);
        int pendingOrders = orderRepository.countPendingOrders(startDate, endDate);
        int cancelledOrders = orderRepository.countCancelledOrders(startDate, endDate);
        double orderChange = getOrderChangePercentage(startDate, endDate, prevStartDate, prevEndDate);

        OrderStatDTO orderStat = new OrderStatDTO();
        orderStat.setTotalOrders(totalOrders);
        orderStat.setCompletedOrders(completedOrders);
        orderStat.setPendingOrders(pendingOrders);
        orderStat.setCancelledOrders(cancelledOrders);
        orderStat.setPercentageChange(orderChange);
        responseDTO.setOrderStat(orderStat);

        // Customer Stat
        int totalCustomers = userRepository.countCustomers();
        int newCustomers = userRepository.countNewCustomers(startDate, endDate);
        double customerChange = userRepository.getCustomerChangePercentage(startDate, endDate, prevStartDate, prevEndDate);

        CustomerStatDTO customerStat = new CustomerStatDTO();
        customerStat.setTotalCustomers(totalCustomers);
        customerStat.setNewCustomers(newCustomers);
        customerStat.setPercentageChange(customerChange);
        responseDTO.setCustomerStat(customerStat);

        // Product Stat
        int totalProducts = productRepository.countProducts();
        int outOfStock = productRepository.countOutOfStockProducts();
        int lowStock = productRepository.countLowStockProducts(10);
        double productChange = getProductChangePercentage(startDate, endDate, prevStartDate, prevEndDate);

        ProductStatDTO productStat = new ProductStatDTO();
        productStat.setTotalProducts(totalProducts);
        productStat.setOutOfStockProducts(outOfStock);
        productStat.setLowStockProducts(lowStock);
        productStat.setPercentageChange(productChange);
        responseDTO.setProductStat(productStat);

        return responseDTO;
    }
}
