package com.devtools.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "devtools.ai.gemini")
@Getter
@Setter
public class GeminiProperties {
    private String apiKey;
    private String model = "gemini-1.5-flash";
    private String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models";
    private int timeoutSeconds = 30;
    private int maxRetries = 2;
}
