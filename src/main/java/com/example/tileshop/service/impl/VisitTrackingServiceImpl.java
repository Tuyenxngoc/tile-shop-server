package com.example.tileshop.service.impl;

import com.example.tileshop.constant.ErrorMessage;
import com.example.tileshop.entity.VisitStatistics;
import com.example.tileshop.exception.BadRequestException;
import com.example.tileshop.repository.VisitStatisticsRepository;
import com.example.tileshop.service.VisitTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitTrackingServiceImpl implements VisitTrackingService {
    private final RedisTemplate<String, String> redisTemplate;

    private final VisitStatisticsRepository visitStatisticsRepository;

    private String getTotalKey(String url, LocalDate date) {// Lượt xem trang
        return "visit:total:" + url + ":" + date;
    }

    private String getUniqueKey(String url, LocalDate date) {// Lượt truy cập
        return "visit:unique:" + url + ":" + date;
    }

    @Override
    public void trackVisit(String url, String ipAddress) {
        if (url.contains(":")) {
            throw new BadRequestException(ErrorMessage.INVALID_URL_FORMAT);
        }
        LocalDate now = LocalDate.now();

        // Lượt xem trang
        String totalKey = getTotalKey(url, now);
        Boolean totalKeyExists = redisTemplate.hasKey(totalKey);
        redisTemplate.opsForValue().increment(totalKey);
        if (Boolean.FALSE.equals(totalKeyExists)) {
            redisTemplate.expire(totalKey, Duration.ofDays(2));
        }

        // Lượt truy cập unique theo IP
        String uniqueKey = getUniqueKey(url, now);
        Boolean uniqueKeyExists = redisTemplate.hasKey(uniqueKey);
        redisTemplate.opsForSet().add(uniqueKey, ipAddress);
        if (Boolean.FALSE.equals(uniqueKeyExists)) {
            redisTemplate.expire(uniqueKey, Duration.ofDays(2));
        }
    }

    @Override
    public long getVisits(LocalDateTime start, LocalDateTime end) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        long dbSum = 0;
        long redisSum = 0;

        // Truy vấn DB nếu có ngày trước hôm nay
        if (startDate.isBefore(today)) {
            LocalDate dbEndDate = endDate.isBefore(today) ? endDate : today.minusDays(1);
            dbSum = visitStatisticsRepository.sumUniqueVisitsByDateRange(startDate, dbEndDate);
        }

        // Redis: các ngày từ hôm nay trở đi
        Set<String> keys = redisTemplate.keys("visit:unique:*:*");
        if (keys != null) {
            for (String key : keys) {
                String[] parts = key.split(":");
                if (parts.length < 4) continue;

                try {
                    LocalDate keyDate = LocalDate.parse(parts[3]);
                    if (!keyDate.isBefore(startDate) && !keyDate.isAfter(endDate)) {
                        Long size = redisTemplate.opsForSet().size(key);
                        redisSum += size != null ? size : 0;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return dbSum + redisSum;
    }

    @Override
    public double getPageViews(LocalDateTime start, LocalDateTime end) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        long dbSum = 0;
        long redisSum = 0;

        // DB: các ngày trước hôm nay
        if (startDate.isBefore(today)) {
            LocalDate dbEndDate = endDate.isBefore(today) ? endDate : today.minusDays(1);
            dbSum = visitStatisticsRepository.sumTotalVisitsByDateRange(startDate, dbEndDate);
        }

        // Redis: các ngày từ hôm nay trở đi
        Set<String> keys = redisTemplate.keys("visit:total:*:*");
        if (keys != null) {
            for (String key : keys) {
                String[] parts = key.split(":");
                if (parts.length < 4) continue;

                try {
                    LocalDate keyDate = LocalDate.parse(parts[3]);
                    if (!keyDate.isBefore(startDate) && !keyDate.isAfter(endDate)) {
                        String value = redisTemplate.opsForValue().get(key);
                        redisSum += value != null ? Long.parseLong(value) : 0;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return dbSum + redisSum;
    }

    @Override
    public void syncStatisticsFromRedis() {
        // Lấy tất cả key lượt truy cập tổng dạng visit:total:*
        Set<String> totalKeys = redisTemplate.keys("visit:total:*");
        if (totalKeys == null || totalKeys.isEmpty()) {
            log.info("Không có dữ liệu truy cập hàng ngày trong Redis.");
            return;
        }

        for (String totalKey : totalKeys) {
            // totalKey: visit:total:<url>:<date>
            // Tách url và date từ key
            String[] parts = totalKey.split(":");
            if (parts.length < 4) continue;

            String url = parts[2];
            LocalDate date = LocalDate.parse(parts[3]);

            // Lấy tổng lượt truy cập
            String totalValue = redisTemplate.opsForValue().get(totalKey);
            long totalVisits = totalValue != null ? Long.parseLong(totalValue) : 0L;

            // Lấy số lượt unique
            String uniqueKey = getUniqueKey(url, date);
            Long uniqueVisitsObj = redisTemplate.opsForSet().size(uniqueKey);
            long uniqueVisits = uniqueVisitsObj != null ? uniqueVisitsObj : 0L;

            // Tìm bản ghi trong MySQL
            VisitStatistics statistics = visitStatisticsRepository
                    .findByUrlAndDate(url, LocalDate.now())
                    .orElse(
                            VisitStatistics.builder()
                                    .url(url)
                                    .date(LocalDate.now())
                                    .build()
                    );

            // Cập nhật hoặc set mới số liệu
            statistics.setTotalVisits(totalVisits);
            statistics.setUniqueVisits(uniqueVisits);

            // Lưu vào MySQL
            visitStatisticsRepository.save(statistics);

            // Xoá key Redis sau khi sync
            redisTemplate.delete(totalKey);
            redisTemplate.delete(uniqueKey);
        }

        log.info("Hoàn tất đồng bộ dữ liệu truy cập hàng ngày từ Redis về MySQL lúc {}", LocalDate.now());
    }
}
