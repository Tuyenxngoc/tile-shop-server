package com.example.tileshop.repository;

import com.example.tileshop.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {
    @Query("SELECT COUNT(v) FROM VisitLog v WHERE v.visitedAt BETWEEN :start AND :end")
    long countTotalVisits(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM VisitLog v WHERE v.visitedAt BETWEEN :start AND :end")
    long countUniqueVisits(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT v.url FROM VisitLog v WHERE DATE(v.visitedAt) = :date")
    List<String> findDistinctUrlsByDate(LocalDate date);

    @Query("SELECT COUNT(v) FROM VisitLog v WHERE v.url = :url AND DATE(v.visitedAt) = :date")
    long countByUrlAndDate(String url, LocalDate date);

    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM VisitLog v WHERE v.url = :url AND DATE(v.visitedAt) = :date")
    long countDistinctIpByUrlAndDate(String url, LocalDate date);

    @Query("SELECT COUNT(v) FROM VisitLog v WHERE v.ipAddress = :ipAddress AND DATE(v.visitedAt) = :date")
    long countByIpAddressAndDate(@Param("ipAddress") String ipAddress, @Param("date") LocalDate date);

    @Transactional
    @Modifying
    @Query("DELETE FROM VisitLog v WHERE DATE(v.visitedAt) = :date")
    void deleteByDate(LocalDate date);
}