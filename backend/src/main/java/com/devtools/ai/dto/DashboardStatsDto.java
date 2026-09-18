package com.devtools.ai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {

    private long totalAnalyses;
    private int usedToday;
    private int dailyLimit;
    private int remainingToday;
    private String plan;
    private String mostCommonError;
    private String mostCommonTechnology;
    private List<AnalysisSummaryDto> recentAnalyses;
}
