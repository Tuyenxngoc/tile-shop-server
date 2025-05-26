package com.example.tileshop.service.impl;

import com.example.tileshop.constant.OrderStatus;
import com.example.tileshop.dto.filter.RevenueStatsFilter;
import com.example.tileshop.dto.filter.TimeFilter;
import com.example.tileshop.dto.statistics.*;
import com.example.tileshop.repository.OrderRepository;
import com.example.tileshop.repository.ProductRepository;
import com.example.tileshop.repository.UserRepository;
import com.example.tileshop.service.StatService;
import com.example.tileshop.service.VisitTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final VisitTrackingService visitTrackingService;

    private double getCustomerChangePercentage(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime prevStartDate, LocalDateTime prevEndDate) {
        int currentCount = userRepository.countCustomers(startDate, endDate);
        int previousCount = userRepository.countCustomers(prevStartDate, prevEndDate);

        if (previousCount == 0) {
            return currentCount > 0 ? 100.0 : 0.0;
        }

        double percentage = ((double) (currentCount - previousCount) / previousCount) * 100;
        return Math.round(percentage * 10.0) / 10.0;
    }

    private double getProductChangePercentage(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime prevStartDate, LocalDateTime prevEndDate) {
        int currentCount = productRepository.countProductsCreatedBetween(startDate, endDate);
        int previousCount = productRepository.countProductsCreatedBetween(prevStartDate, prevEndDate);

        if (previousCount == 0) {
            return currentCount > 0 ? 100.0 : 0.0;
        }

        double percentage = ((double) (currentCount - previousCount) / previousCount) * 100;
        return Math.round(percentage * 10.0) / 10.0;
    }

    private double getOrderChangePercentage(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime prevStartDate, LocalDateTime prevEndDate) {
        int currentCount = orderRepository.countOrders(startDate, endDate);
        int previousCount = orderRepository.countOrders(prevStartDate, prevEndDate);

        if (previousCount == 0) {
            return currentCount > 0 ? 100.0 : 0.0;
        }

        double percentage = ((double) (currentCount - previousCount) / previousCount) * 100;
        return Math.round(percentage * 10.0) / 10.0;
    }

    private double getRevenueChangePercentage(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime prevStartDate, LocalDateTime prevEndDate) {
        double currentRevenue = orderRepository.getTotalRevenue(startDate, endDate);
        double previousRevenue = orderRepository.getTotalRevenue(prevStartDate, prevEndDate);

        if (previousRevenue == 0) {
            return currentRevenue > 0 ? 100.0 : 0.0;
        }

        double percentage = ((currentRevenue - previousRevenue) / previousRevenue) * 100;
        return Math.round(percentage * 10.0) / 10.0;
    }

    @Override
    public DashboardStatsResponseDTO getStatistics(LocalDate date) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59, 999_999_999);  // 23:59:59.999999999 ngày date

        LocalDate prevDate = date.minusDays(1);
        LocalDateTime prevStartDate = prevDate.atStartOfDay();
        LocalDateTime prevEndDate = prevDate.atTime(23, 59, 59, 999_999_999);  // 23:59:59.999999999 ngày prevDate

        DashboardStatsResponseDTO responseDTO = new DashboardStatsResponseDTO();

        // Revenue Stat
        double totalRevenue = orderRepository.getTotalRevenue(startDate, endDate);
        double revenueChange = getRevenueChangePercentage(startDate, endDate, prevStartDate, prevEndDate);

        RevenueStatDTO revenueStat = new RevenueStatDTO();
        revenueStat.setTotalRevenue(totalRevenue);
        revenueStat.setPercentageChange(revenueChange);
        revenueStat.setCurrency("VND");
        revenueStat.setPeriod(startDate + " - " + endDate);
        responseDTO.setRevenueStat(revenueStat);

        // Order Stat
        int totalOrders = orderRepository.countOrders(startDate, endDate);
        int completedOrders = orderRepository.countOrdersByStatus(OrderStatus.DELIVERED, startDate, endDate);
        int pendingOrders = orderRepository.countOrdersByStatus(OrderStatus.PENDING, startDate, endDate);
        int cancelledOrders = orderRepository.countOrdersByStatus(OrderStatus.CANCELLED, startDate, endDate);
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
        int newCustomers = userRepository.countCustomers(startDate, endDate);
        double customerChange = getCustomerChangePercentage(startDate, endDate, prevStartDate, prevEndDate);

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

    @Override
    public List<TopSellingProductDTO> getTopSellingProducts(TimeFilter filter) {
        Pageable pageable = PageRequest.of(0, 5);

        LocalDateTime startDate = filter.getStartDate() != null
                ? filter.getStartDate().atStartOfDay()
                : null;

        LocalDateTime endDate = filter.getEndDate() != null
                ? filter.getEndDate().atTime(23, 59, 59)
                : null;

        return productRepository.findTopSellingProducts(startDate, endDate, pageable);
    }

    @Override
    public List<TopCustomerDTO> getTopCustomers(TimeFilter filter) {
        Pageable pageable = PageRequest.of(0, 5);

        LocalDateTime startDate = filter.getStartDate() != null
                ? filter.getStartDate().atStartOfDay()
                : null;

        LocalDateTime endDate = filter.getEndDate() != null
                ? filter.getEndDate().atTime(23, 59, 59)
                : null;

        return userRepository.findTopCustomers(startDate, endDate, pageable);
    }

    @Override
    public List<RecentOrderDTO> getRecentOrders() {
        Pageable pageable = PageRequest.of(0, 5);

        return orderRepository.findRecentOrders(pageable);
    }

    @Override
    public List<PointDTO> getRevenueStats(RevenueStatsFilter filter) {
        if (filter == null) {
            return Collections.emptyList();
        }

        LocalDate date = filter.getDate();
        String type = filter.getType();

        if (date == null || type == null) {
            return Collections.emptyList();
        }

        List<PointDTO> results = new ArrayList<>();
        switch (type) {
            case "day" -> {
                LocalDateTime dayStart = date.atStartOfDay();
                for (int hour = 0; hour < 24; hour++) {
                    LocalDateTime start = dayStart.plusHours(hour);
                    LocalDateTime end = start.plusHours(1).minusSeconds(1);

                    double revenue = orderRepository.getTotalRevenue(start, end);
                    long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();
                    results.add(new PointDTO(timestamp, revenue));
                }
            }
            case "week" -> {
                LocalDate weekStart = date.with(DayOfWeek.MONDAY);
                LocalDate weekEnd = date.with(DayOfWeek.SUNDAY);
                LocalDate current = weekStart;
                while (!current.isAfter(weekEnd)) {
                    LocalDateTime start = current.atStartOfDay();
                    LocalDateTime end = current.atTime(LocalTime.MAX);

                    double revenue = orderRepository.getTotalRevenue(start, end);
                    long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();
                    results.add(new PointDTO(timestamp, revenue));

                    current = current.plusDays(1);
                }
            }
            case "month" -> {
                LocalDate monthStart = date.withDayOfMonth(1);
                LocalDate monthEnd = date.withDayOfMonth(date.lengthOfMonth());
                LocalDate current = monthStart;
                while (!current.isAfter(monthEnd)) {
                    LocalDateTime start = current.atStartOfDay();
                    LocalDateTime end = current.atTime(LocalTime.MAX);

                    double revenue = orderRepository.getTotalRevenue(start, end);
                    long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();
                    results.add(new PointDTO(timestamp, revenue));
                    current = current.plusDays(1);
                }
            }
            case "year" -> {
                Year year = Year.of(date.getYear());
                for (int month = 1; month <= 12; month++) {
                    LocalDate monthStart = year.atMonth(month).atDay(1);
                    LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
                    LocalDateTime start = monthStart.atStartOfDay();
                    LocalDateTime end = monthEnd.atTime(LocalTime.MAX);

                    double revenue = orderRepository.getTotalRevenue(start, end);
                    long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();
                    results.add(new PointDTO(timestamp, revenue));
                }
            }
            default -> {
                return results;
            }
        }
        return results;
    }

    @Override
    public List<CategoryRevenueChartDTO> getRevenueByCategory(RevenueStatsFilter filter) {
        if (filter == null) {
            return Collections.emptyList();
        }

        LocalDate date = filter.getDate();
        String type = filter.getType();

        if (date == null || type == null) {
            return Collections.emptyList();
        }

        LocalDate startDate;
        LocalDate endDate;

        switch (type) {
            case "day" -> {
                startDate = date;
                endDate = date;
            }
            case "week" -> {
                startDate = date.with(DayOfWeek.MONDAY);
                endDate = date.with(DayOfWeek.SUNDAY);
            }
            case "month" -> {
                startDate = date.withDayOfMonth(1);
                endDate = date.withDayOfMonth(date.lengthOfMonth());
            }
            case "year" -> {
                startDate = date.withDayOfYear(1);
                endDate = date.withDayOfYear(date.lengthOfYear());
            }
            default -> {
                return Collections.emptyList();
            }
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        return orderRepository.getRevenueByCategory(start, end);
    }

    @Override
    public Map<String, MetricDTO> getChartData(ChartDataFilter filter) {
        Map<String, MetricDTO> metrics = new LinkedHashMap<>();
        LocalDate date = filter.getDate();
        String type = filter.getType();

        List<String> allKeys = List.of("revenue", "orders", "conversion", "visits", "pageviews");
        for (String key : allKeys) {
            double total = 0;
            double previousTotal;
            List<PointDTO> points = new ArrayList<>();
            LocalDateTime prevStart, prevEnd;

            switch (type) {
                case "day" -> {
                    LocalDateTime dayStart = date.atStartOfDay();
                    prevStart = dayStart.minusDays(1);
                    prevEnd = prevStart.plusDays(1).minusSeconds(1);

                    for (int hour = 0; hour < 24; hour++) {
                        LocalDateTime start = dayStart.plusHours(hour);
                        LocalDateTime end = start.plusHours(1).minusSeconds(1);

                        double value = getValueByKey(key, start, end);
                        long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();
                        points.add(new PointDTO(timestamp, value));
                        total += value;
                    }
                }
                case "week" -> {
                    LocalDate weekStart = date.with(DayOfWeek.MONDAY);
                    prevStart = weekStart.minusWeeks(1).atStartOfDay();
                    prevEnd = prevStart.plusDays(6).with(LocalTime.MAX);

                    for (int i = 0; i < 7; i++) {
                        LocalDate current = weekStart.plusDays(i);
                        LocalDateTime start = current.atStartOfDay();
                        LocalDateTime end = current.atTime(LocalTime.MAX);

                        double value = getValueByKey(key, start, end);
                        long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();
                        points.add(new PointDTO(timestamp, value));
                        total += value;
                    }
                }
                case "month" -> {
                    LocalDate current = date.withDayOfMonth(1);
                    LocalDate endOfMonth = current.withDayOfMonth(current.lengthOfMonth());

                    prevStart = current.minusMonths(1).withDayOfMonth(1).atStartOfDay();
                    prevEnd = current.minusDays(1).atTime(LocalTime.MAX);

                    while (!current.isAfter(endOfMonth)) {
                        LocalDateTime start = current.atStartOfDay();
                        LocalDateTime end = current.atTime(LocalTime.MAX);

                        double value = getValueByKey(key, start, end);
                        long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();
                        points.add(new PointDTO(timestamp, value));
                        total += value;

                        current = current.plusDays(1);
                    }
                }
                case "year" -> {
                    Year year = Year.of(date.getYear());

                    prevStart = year.minusYears(1).atDay(1).atStartOfDay();
                    prevEnd = year.minusYears(1).atMonth(12).atEndOfMonth().atTime(LocalTime.MAX);

                    for (int month = 1; month <= 12; month++) {
                        LocalDate firstDay = year.atMonth(month).atDay(1);
                        LocalDateTime start = firstDay.atStartOfDay();
                        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
                        LocalDateTime end = lastDay.atTime(LocalTime.MAX);

                        double value = getValueByKey(key, start, end);
                        long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();
                        points.add(new PointDTO(timestamp, value));
                        total += value;
                    }
                }
                default -> {
                    continue;
                }
            }

            previousTotal = getValueByKey(key, prevStart, prevEnd);
            double chainRatio;
            if (previousTotal == 0) {
                chainRatio = (total == 0) ? 0 : 100;
            } else {
                chainRatio = ((total - previousTotal) / previousTotal) * 100;
            }
            chainRatio = Math.round(chainRatio * 100.0) / 100.0;
            metrics.put(key, new MetricDTO(total, chainRatio, points));
        }

        return metrics;
    }

    private double getValueByKey(String key, LocalDateTime start, LocalDateTime end) {
        return switch (key) {
            case "revenue" -> orderRepository.getTotalRevenue(start, end);
            case "orders" -> orderRepository.countOrders(start, end);
            case "conversion" -> {
                long totalOrders = orderRepository.countDistinctCustomersWithOrders(start, end);
                long totalVisits = visitTrackingService.getVisits(start, end);
                if (totalVisits == 0) {
                    yield 0.0;
                } else {
                    double ratio = ((double) totalOrders / totalVisits) * 100;
                    yield Math.round(ratio * 100.0) / 100.0;
                }
            }
            case "visits" -> visitTrackingService.getVisits(start, end);
            case "pageviews" -> visitTrackingService.getPageViews(start, end);
            default -> 0;
        };
    }
}
