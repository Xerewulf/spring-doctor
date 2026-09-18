package com.devtools.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "error_type", length = 150)
    private String errorType;

    @Column(nullable = false, length = 100)
    private String technology;

    @Lob
    @Column(nullable = false)
    private String summary;

    @Lob
    @Column(name = "raw_error_text_redacted")
    private String rawErrorTextRedacted;

    @Lob
    @Column(name = "diagnosis_json", nullable = false)
    private String diagnosisJson;

    @Column(name = "is_saved_input", nullable = false)
    @Builder.Default
    private Boolean isSavedInput = false;

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
