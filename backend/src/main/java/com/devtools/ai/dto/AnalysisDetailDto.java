package com.devtools.ai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisDetailDto {

    private Long id;
    private String title;
    private String errorType;
    private String technology;
    private String summary;
    private String rawErrorTextRedacted;
    private DiagnosisDto diagnosis;
    private boolean isSavedInput;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
