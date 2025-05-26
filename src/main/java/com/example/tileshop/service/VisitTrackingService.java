package com.example.tileshop.service;

public interface VisitTrackingService {
    void trackVisit(String url, String ipAddress);

    long getTotalVisit(String url);

    long getUniqueVisitToday(String url);

    void syncStatisticsFromRedis();
}
