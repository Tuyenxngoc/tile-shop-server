package com.example.tileshop.repository;

import com.example.tileshop.entity.VisitStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VisitStatisticsRepository extends JpaRepository<VisitStatistics, Long> {
    Optional<VisitStatistics> findByUrlAndDate(String url, LocalDate now);
}
