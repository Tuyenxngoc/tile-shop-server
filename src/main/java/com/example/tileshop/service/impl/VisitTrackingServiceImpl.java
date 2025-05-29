package com.example.tileshop.service.impl;

import com.example.tileshop.entity.VisitLog;
import com.example.tileshop.entity.VisitStatistics;
import com.example.tileshop.repository.VisitLogRepository;
import com.example.tileshop.repository.VisitStatisticsRepository;
import com.example.tileshop.service.VisitTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitTrackingServiceImpl implements VisitTrackingService {
    private static final int MAX_VISITS_PER_IP_PER_DAY = 100;

    private final VisitLogRepository visitLogRepository;

    private final VisitStatisticsRepository visitStatisticsRepository;

    @Override
    public void trackVisit(String url, String ipAddress) {
        LocalDate today = LocalDate.now();

        long visitCount = visitLogRepository.countByIpAddressAndDate(ipAddress, today);
        if (visitCount >= MAX_VISITS_PER_IP_PER_DAY) {
            return;
        }

        visitLogRepository.save(VisitLog.builder()
                .url(url)
                .ipAddress(ipAddress)
                .visitedAt(LocalDateTime.now())
                .build());
    }

    @Override
    public long getVisits(LocalDateTime start, LocalDateTime end) {
        return visitLogRepository.countUniqueVisits(start, end);
    }

    @Override
    public long getPageViews(LocalDateTime start, LocalDateTime end) {
        return visitLogRepository.countTotalVisits(start, end);
    }

    @Override
    public void aggregateDailyStatistics(LocalDate date) {
        // Lấy tất cả URL truy cập trong ngày đó
        List<String> urls = visitLogRepository.findDistinctUrlsByDate(date);

        for (String url : urls) {
            long totalVisits = visitLogRepository.countByUrlAndDate(url, date);
            long uniqueVisits = visitLogRepository.countDistinctIpByUrlAndDate(url, date);

            VisitStatistics stat = visitStatisticsRepository
                    .findByUrlAndDate(url, date)
                    .orElse(VisitStatistics.builder()
                            .url(url)
                            .date(date)
                            .build());

            stat.setTotalVisits(totalVisits);
            stat.setUniqueVisits(uniqueVisits);
            visitStatisticsRepository.save(stat);
        }

//        visitLogRepository.deleteByDate(date);
    }
}
