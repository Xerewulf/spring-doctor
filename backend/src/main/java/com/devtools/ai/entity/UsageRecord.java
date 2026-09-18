package com.devtools.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "client_ip_hash", nullable = false, length = 64)
    private String clientIpHash;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "analysis_count", nullable = false)
    @Builder.Default
    private Integer analysisCount = 0;

    @Column(name = "request_type", nullable = false, length = 50)
    @Builder.Default
    private String requestType = "ERROR_ANALYSIS";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
