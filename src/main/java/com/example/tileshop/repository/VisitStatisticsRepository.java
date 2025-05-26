package com.example.tileshop.repository;

import com.example.tileshop.entity.VisitStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VisitStatisticsRepository extends JpaRepository<VisitStatistics, Long> {
    Optional<VisitStatistics> findByUrlAndDate(String url, LocalDate now);

    @Query("SELECT COALESCE(SUM(v.uniqueVisits), 0) FROM VisitStatistics v WHERE v.date BETWEEN :startDate AND :endDate")
    long sumUniqueVisitsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(v.totalVisits), 0) FROM VisitStatistics v WHERE v.date BETWEEN :startDate AND :endDate")
    long sumTotalVisitsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
