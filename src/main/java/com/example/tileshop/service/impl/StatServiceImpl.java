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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final VisitTrackingService visitTrackingService;

    private double getCustomerChangePercentage(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime prevStartDate, LocalDateTime prevEndDate) {
        int currentCount = userRepository.countUsers(startDate, endDate);
        int previousCount = userRepository.countUsers(prevStartDate, prevEndDate);

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
        long totalCustomers = userRepository.count();
        int newCustomers = userRepository.countUsers(startDate, endDate);
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

        return userRepository.findTopUsers(startDate, endDate, pageable);
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

            // Biến riêng cho conversion: tỉ lệ chuyển đổi
            double totalOrders = 0;
            double totalVisits = 0;

            switch (type) {
                case "day" -> {
                    LocalDateTime dayStart = date.atStartOfDay(); // Lấy thời điểm bắt đầu của ngày hiện tại (00:00:00)
                    prevStart = dayStart.minusDays(1);// Thời điểm bắt đầu của ngày trước đó
                    prevEnd = prevStart.plusDays(1).minusSeconds(1); // Thời điểm kết thúc của ngày trước đó (23:59:59)

                    for (int hour = 0; hour < 24; hour++) {
                        LocalDateTime start = dayStart.plusHours(hour);// Thời điểm bắt đầu của từng giờ trong ngày
                        LocalDateTime end = start.plusHours(1).minusSeconds(1);// Thời điểm kết thúc của từng giờ (59:59)
                        long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();

                        if (key.equals("conversion")) {
                            long orders = orderRepository.countDistinctCustomersWithOrders(start, end);
                            long visits = visitTrackingService.getVisits(start, end);
                            double ratio = (visits == 0) ? 0.0 : ((double) orders / visits) * 100;
                            ratio = Math.round(ratio * 100.0) / 100.0;
                            points.add(new PointDTO(timestamp, ratio));

                            totalOrders += orders;
                            totalVisits += visits;
                        } else {
                            double value = getValueByKey(key, start, end);
                            points.add(new PointDTO(timestamp, value));
                            total += value;
                        }
                    }
                }
                case "week" -> {
                    LocalDate weekStart = date.with(DayOfWeek.MONDAY);// Lấy ngày thứ 2 trong tuần chứa date
                    prevStart = weekStart.minusWeeks(1).atStartOfDay(); // Thời điểm bắt đầu của tuần trước
                    prevEnd = prevStart.plusDays(6).with(LocalTime.MAX); // Thời điểm kết thúc của tuần trước (23:59:59)

                    for (int i = 0; i < 7; i++) {
                        LocalDate current = weekStart.plusDays(i);// Ngày thứ i trong tuần hiện tại
                        LocalDateTime start = current.atStartOfDay();// 00:00:00 của ngày thứ i
                        LocalDateTime end = current.atTime(LocalTime.MAX); // 23:59:59 của ngày thứ i
                        long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();

                        if (key.equals("conversion")) {
                            long orders = orderRepository.countDistinctCustomersWithOrders(start, end);
                            long visits = visitTrackingService.getVisits(start, end);
                            double ratio = (visits == 0) ? 0.0 : ((double) orders / visits) * 100;
                            ratio = Math.round(ratio * 100.0) / 100.0;
                            points.add(new PointDTO(timestamp, ratio));

                            totalOrders += orders;
                            totalVisits += visits;
                        } else {
                            double value = getValueByKey(key, start, end);
                            points.add(new PointDTO(timestamp, value));
                            total += value;
                        }
                    }
                }
                case "month" -> {
                    LocalDate current = date.withDayOfMonth(1); // Ngày đầu tiên của tháng hiện tại
                    LocalDate endOfMonth = current.withDayOfMonth(current.lengthOfMonth()); // Ngày cuối cùng của tháng hiện tại

                    prevStart = current.minusMonths(1).withDayOfMonth(1).atStartOfDay();// Bắt đầu của tháng trước
                    prevEnd = current.minusDays(1).atTime(LocalTime.MAX); // Kết thúc của tháng trước (ngày cuối tháng trước - 23:59:59)

                    while (!current.isAfter(endOfMonth)) {
                        LocalDateTime start = current.atStartOfDay(); // 00:00:00 của từng ngày trong tháng
                        LocalDateTime end = current.atTime(LocalTime.MAX);// 23:59:59 của từng ngày
                        long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();

                        if (key.equals("conversion")) {
                            long orders = orderRepository.countDistinctCustomersWithOrders(start, end);
                            long visits = visitTrackingService.getVisits(start, end);
                            double ratio = (visits == 0) ? 0.0 : ((double) orders / visits) * 100;
                            ratio = Math.round(ratio * 100.0) / 100.0;
                            points.add(new PointDTO(timestamp, ratio));

                            totalOrders += orders;
                            totalVisits += visits;
                        } else {
                            double value = getValueByKey(key, start, end);
                            points.add(new PointDTO(timestamp, value));
                            total += value;
                        }

                        current = current.plusDays(1); // Sang ngày tiếp theo
                    }
                }
                case "year" -> {
                    Year year = Year.of(date.getYear()); // Năm hiện tại (ví dụ: 2024)

                    prevStart = year.minusYears(1).atDay(1).atStartOfDay();// Ngày 1/1 của năm trước
                    prevEnd = year.minusYears(1).atMonth(12).atEndOfMonth().atTime(LocalTime.MAX);// Ngày 31/12 của năm trước - 23:59:59

                    for (int month = 1; month <= 12; month++) {
                        LocalDate firstDay = year.atMonth(month).atDay(1); // Ngày đầu tiên của tháng thứ month trong năm hiện tại
                        LocalDateTime start = firstDay.atStartOfDay();// 00:00:00 ngày đầu tháng
                        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());// Ngày cuối tháng
                        LocalDateTime end = lastDay.atTime(LocalTime.MAX);// 23:59:59 ngày cuối tháng
                        long timestamp = start.atZone(ZoneId.systemDefault()).toEpochSecond();

                        if (key.equals("conversion")) {
                            long orders = orderRepository.countDistinctCustomersWithOrders(start, end);
                            long visits = visitTrackingService.getVisits(start, end);
                            double ratio = (visits == 0) ? 0.0 : ((double) orders / visits) * 100;
                            ratio = Math.round(ratio * 100.0) / 100.0;
                            points.add(new PointDTO(timestamp, ratio));

                            totalOrders += orders;
                            totalVisits += visits;
                        } else {
                            double value = getValueByKey(key, start, end);
                            points.add(new PointDTO(timestamp, value));
                            total += value;
                        }
                    }
                }
                default -> {
                    continue;
                }
            }

            // Tính tổng cho conversion
            if (key.equals("conversion")) {
                total = (totalVisits == 0) ? 0.0 : (totalOrders / totalVisits) * 100;
                total = Math.round(total * 100.0) / 100.0;
            }

            // Tính previousTotal
            if (key.equals("conversion")) {
                long prevOrders = orderRepository.countDistinctCustomersWithOrders(prevStart, prevEnd);
                long prevVisits = visitTrackingService.getVisits(prevStart, prevEnd);
                previousTotal = (prevVisits == 0) ? 0.0 : ((double) prevOrders / prevVisits) * 100;
                previousTotal = Math.round(previousTotal * 100.0) / 100.0;
            } else {
                previousTotal = getValueByKey(key, prevStart, prevEnd);
            }

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
            case "visits" -> visitTrackingService.getVisits(start, end);
            case "pageviews" -> visitTrackingService.getPageViews(start, end);
            default -> 0;
        };
    }

    @Override
    public byte[] exportChartDataToExcel(ChartDataFilter filter) {
        String timeLabel;
        DateTimeFormatter formatter;

        if ("day".equals(filter.getType())) {
            timeLabel = "Thời gian";
            formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        } else {
            timeLabel = "Ngày";
            formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        }

        List<ShopStat> stats = getShopStatsByFilter(filter);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("shop-stats");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            headerCellStyle.setWrapText(true);

            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Tạo tiêu đề cột
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    timeLabel,
                    "Tổng doanh số (VND)",
                    "Tổng số đơn hàng",
                    "Doanh số trên mỗi đơn hàng",
                    "Tổng số lượt xem sản phẩm",
                    "Số lượt truy cập",
                    "Tỉ lệ mua hàng, đã thanh toán",
                    "Đơn đã hủy",
                    "Doanh số đơn hủy",
                    "Đơn đã hoàn trả / hoàn tiền",
                    "Doanh số các đơn Trả hàng/Hoàn tiền",
                    "Số người dùng",
                    "Số người dùng mới"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowNum = 1;
            for (ShopStat stat : stats) {
                Row row = sheet.createRow(rowNum++);

                Object[] values = new Object[]{
                        stat.getTime().format(formatter),
                        stat.getTotalSales(),
                        stat.getTotalOrders(),
                        stat.getSalesPerOrder(),
                        stat.getProductViews(),
                        stat.getVisits(),
                        stat.getPurchaseRate(),
                        stat.getCanceledOrders(),
                        stat.getCanceledSales(),
                        stat.getReturnedOrders(),
                        stat.getReturnedSales(),
                        stat.getUserCount(),
                        stat.getNewUserCount()
                };

                for (int i = 0; i < values.length; i++) {
                    Cell cell = row.createCell(i);
                    Object val = values[i];
                    if (val instanceof String) {
                        cell.setCellValue((String) val);
                    } else if (val instanceof Number) {
                        double doubleVal = ((Number) val).doubleValue();
                        double roundedVal = Math.round(doubleVal * 100.0) / 100.0;
                        cell.setCellValue(roundedVal);
                    }
                    cell.setCellStyle(dataCellStyle);
                }
            }

            // Auto size cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error while generating shop stats Excel report", e);
            throw new RuntimeException("Error while generating shop stats Excel report", e);
        }
    }

    private List<ShopStat> getShopStatsByFilter(ChartDataFilter filter) {
        List<ShopStat> shopStats = new ArrayList<>();
        LocalDate date = filter.getDate();
        String type = filter.getType();

        switch (type) {
            case "day" -> {
                LocalDateTime dayStart = date.atStartOfDay(); // Lấy thời điểm bắt đầu của ngày hiện tại (00:00:00)

                for (int hour = 0; hour < 24; hour++) {
                    LocalDateTime start = dayStart.plusHours(hour);// Thời điểm bắt đầu của từng giờ trong ngày
                    LocalDateTime end = start.plusHours(1).minusSeconds(1);// Thời điểm kết thúc của từng giờ (59:59)

                    ShopStat shopStat = createStat(start, end);
                    shopStats.add(shopStat);
                }
            }
            case "week" -> {
                LocalDate weekStart = date.with(DayOfWeek.MONDAY);// Lấy ngày thứ 2 trong tuần chứa date

                for (int i = 0; i < 7; i++) {
                    LocalDate current = weekStart.plusDays(i);// Ngày thứ i trong tuần hiện tại
                    LocalDateTime start = current.atStartOfDay();// 00:00:00 của ngày thứ i
                    LocalDateTime end = current.atTime(LocalTime.MAX); // 23:59:59 của ngày thứ i

                    ShopStat shopStat = createStat(start, end);
                    shopStats.add(shopStat);
                }
            }
            case "month" -> {
                LocalDate current = date.withDayOfMonth(1); // Ngày đầu tiên của tháng hiện tại
                LocalDate endOfMonth = current.withDayOfMonth(current.lengthOfMonth()); // Ngày cuối cùng của tháng hiện tại

                while (!current.isAfter(endOfMonth)) {
                    LocalDateTime start = current.atStartOfDay(); // 00:00:00 của từng ngày trong tháng
                    LocalDateTime end = current.atTime(LocalTime.MAX);// 23:59:59 của từng ngày

                    ShopStat shopStat = createStat(start, end);
                    shopStats.add(shopStat);

                    current = current.plusDays(1); // Sang ngày tiếp theo
                }
            }
            case "year" -> {
                Year year = Year.of(date.getYear()); // Năm hiện tại (ví dụ: 2024)

                for (int month = 1; month <= 12; month++) {
                    LocalDate firstDay = year.atMonth(month).atDay(1); // Ngày đầu tiên của tháng thứ month trong năm hiện tại
                    LocalDateTime start = firstDay.atStartOfDay();// 00:00:00 ngày đầu tháng
                    LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());// Ngày cuối tháng
                    LocalDateTime end = lastDay.atTime(LocalTime.MAX);// 23:59:59 ngày cuối tháng

                    ShopStat shopStat = createStat(start, end);
                    shopStats.add(shopStat);
                }
            }
        }

        return shopStats;
    }

    private ShopStat createStat(LocalDateTime start, LocalDateTime end) {
        ShopStat stat = new ShopStat();
        stat.setTime(start);

        // Tổng doanh số
        double totalSales = orderRepository.getTotalRevenue(start, end);
        stat.setTotalSales(totalSales);

        // Tổng số đơn
        int totalOrders = orderRepository.countOrdersByStatus(OrderStatus.DELIVERED, start, end);
        stat.setTotalOrders(totalOrders);

        // Doanh số / đơn
        double salesPerOrder = (totalOrders > 0) ? totalSales / totalOrders : 0;
        stat.setSalesPerOrder(salesPerOrder);

        // Lượt xem sản phẩm
        long productViews = visitTrackingService.getPageViews(start, end);
        stat.setProductViews(productViews);

        // Lượt truy cập
        long visits = visitTrackingService.getVisits(start, end);
        stat.setVisits(visits);

        // Tỉ lệ mua hàng (VD: tổng đơn / lượt truy cập * 100)
        double purchaseRate = 0;
        if (stat.getVisits() > 0) {
            long customersWithOrders = orderRepository.countDistinctCustomersWithOrders(start, end);
            purchaseRate = (double) customersWithOrders / stat.getVisits() * 100;
        }
        stat.setPurchaseRate(purchaseRate);

        // Đơn hủy
        int canceledOrders = orderRepository.countOrdersByStatus(OrderStatus.CANCELLED, start, end);
        stat.setCanceledOrders(canceledOrders);

        // Doanh số đơn hủy
        double canceledSales = orderRepository.getRevenueByStatus(OrderStatus.CANCELLED, start, end);
        stat.setCanceledSales(canceledSales);

        // Đơn trả hoàn
        int returnedOrders = orderRepository.countOrdersByStatus(OrderStatus.RETURNED, start, end);
        stat.setReturnedOrders(returnedOrders);

        // Doanh số trả hoàn
        double returnedSales = orderRepository.getRevenueByStatus(OrderStatus.RETURNED, start, end);
        stat.setReturnedSales(returnedSales);

        // Số người dùng hiện có (tổng)
        long totalUsers = userRepository.countUsersUpTo(end);
        stat.setUserCount(totalUsers);

        // Số người dùng mới
        int newUserCount = userRepository.countUsers(start, end);
        stat.setNewUserCount(newUserCount);

        return stat;
    }

    @Getter
    @Setter
    public static class ShopStat {
        private LocalDateTime time;     // thời gian hoặc ngày
        private double totalSales;        // tổng doanh số
        private int totalOrders;        // tổng đơn hàng
        private double salesPerOrder;   // doanh số trên đơn
        private long productViews;       // lượt xem sản phẩm
        private long visits;             // lượt truy cập
        private double purchaseRate;    // tỉ lệ mua hàng (%)
        private int canceledOrders;     // đơn hủy
        private double canceledSales;     // doanh số đơn hủy
        private int returnedOrders;     // đơn trả hoàn
        private double returnedSales;    // doanh số đơn trả hoàn
        private long userCount;          // số người dùng
        private int newUserCount;       // người dùng mới
    }
}
