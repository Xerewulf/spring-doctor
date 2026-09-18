package com.devtools.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorAnalysisRequest {

    @NotBlank(message = "Error or stack trace cannot be empty")
    @Size(max = 64000, message = "Error input exceeds maximum allowed limit (64,000 characters)")
    private String errorText;

    private String technology;

    private String context;

    @Builder.Default
    private Boolean saveInput = false;
}
