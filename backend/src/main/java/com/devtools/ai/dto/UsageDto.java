package com.devtools.ai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageDto {

    private String plan;
    private int dailyLimit;
    private int usedToday;
    private int remainingToday;
    private boolean authenticated;
}
