package com.devtools.ai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisSummaryDto {

    private Long id;
    private String title;
    private String errorType;
    private String technology;
    private String summary;
    private boolean isSavedInput;
    private LocalDateTime createdAt;
}
