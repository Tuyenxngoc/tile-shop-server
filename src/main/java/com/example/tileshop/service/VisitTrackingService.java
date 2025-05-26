package com.example.tileshop.service;

import java.time.LocalDateTime;

public interface VisitTrackingService {
    void trackVisit(String url, String ipAddress);

    long getVisits(LocalDateTime start, LocalDateTime end);

    double getPageViews(LocalDateTime start, LocalDateTime end);

    void syncStatisticsFromRedis();
}
