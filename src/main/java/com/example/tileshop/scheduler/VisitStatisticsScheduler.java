package com.example.tileshop.scheduler;

import com.example.tileshop.service.VisitTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class VisitStatisticsScheduler {

    private final VisitTrackingService visitTrackingService;

    // Chạy lúc 23:59:00 mỗi ngày
    @Scheduled(cron = "0 59 23 * * *")
    public void syncDailyStatistics() {
        visitTrackingService.syncStatisticsFromRedis();
        log.info("Đã đồng bộ dữ liệu thống kê từ Redis về MySQL lúc {}", LocalDateTime.now());
    }
}
