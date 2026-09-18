package com.devtools.ai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestedFixDto {
    private String title;
    private String description;
    private String code;
    @Builder.Default
    private String language = "java";
}
