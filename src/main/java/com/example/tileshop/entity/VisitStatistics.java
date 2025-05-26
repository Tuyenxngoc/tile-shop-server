package com.example.tileshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "visit_statistics",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_VISIT_STATISTICS_URL_DATE",
                        columnNames = {"url", "date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Long totalVisits;

    @Column(nullable = false)
    private Long uniqueVisits;
}
