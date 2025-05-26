package com.example.tileshop.service.impl;

import com.example.tileshop.entity.VisitStatistics;
import com.example.tileshop.repository.VisitStatisticsRepository;
import com.example.tileshop.service.VisitTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
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
        LocalDate now = LocalDate.now();

        // Lượt xem trang (tăng 1 mỗi lần truy cập)
        String totalKey = getTotalKey(url, now);
        redisTemplate.opsForValue().increment(totalKey);

        // Lượt truy cập duy nhất (theo IP và ngày)
        String uniqueKey = getUniqueKey(url, now);
        redisTemplate.opsForSet().add(uniqueKey, ipAddress);

        // Đặt TTL cho key để tự xoá sau vài ngày
        redisTemplate.expire(uniqueKey, Duration.ofDays(2));
        redisTemplate.expire(totalKey, Duration.ofDays(2));
    }

    @Override
    public long getTotalVisit(String url) {
        String totalKey = getTotalKey(url, LocalDate.now());
        String value = redisTemplate.opsForValue().get(totalKey);
        return value == null ? 0 : Long.parseLong(value);
    }

    @Override
    public long getUniqueVisitToday(String url) {
        String uniqueKey = getUniqueKey(url, LocalDate.now());
        Long size = redisTemplate.opsForSet().size(uniqueKey);
        return size != null ? size : 0L;
    }

    @Override
    public void syncStatisticsFromRedis() {
        // Lấy tất cả key lượt truy cập tổng dạng visit:total:*
        Set<String> totalKeys = redisTemplate.keys("visit:total:*");
        if (totalKeys.isEmpty()) {
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
