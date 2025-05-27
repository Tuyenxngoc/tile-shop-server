package com.example.tileshop.scheduler;

import com.example.tileshop.service.VisitTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class VisitStatisticsScheduler {
    private final VisitTrackingService visitTrackingService;

    @Scheduled(cron = "0 0 1 * * *") // mỗi ngày lúc 01:00 sáng
    public void runDailyAggregation() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        visitTrackingService.aggregateDailyStatistics(yesterday);
    }
}
