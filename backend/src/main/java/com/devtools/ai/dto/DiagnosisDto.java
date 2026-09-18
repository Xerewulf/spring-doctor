package com.devtools.ai.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisDto {

    private String summary;
    private String rootCause;
    private String confidence; // HIGH, MEDIUM, LOW
    private String severity;   // LOW, MEDIUM, HIGH, CRITICAL
    private String whyItHappens;

    @Builder.Default
    private List<SuggestedFixDto> suggestedFixes = new ArrayList<>();

    @Builder.Default
    private List<String> thingsToCheck = new ArrayList<>();

    @Builder.Default
    private List<String> relatedTechnologies = new ArrayList<>();

    @Builder.Default
    private List<String> possibleCauses = new ArrayList<>();

    private String detectedException;
    private String sanitizationNotice;
    private Long analysisId;
    private Boolean wasInputSaved;
}
