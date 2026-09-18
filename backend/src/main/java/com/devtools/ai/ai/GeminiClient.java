package com.devtools.ai.ai;

import com.devtools.ai.config.GeminiProperties;
import com.devtools.ai.exception.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final GeminiProperties geminiProperties;
    private final ObjectMapper objectMapper;
    private final WebClient webClient = WebClient.builder().build();

    public String generateContent(String prompt) {
        String apiKey = geminiProperties.getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty() || "null".equalsIgnoreCase(apiKey)) {
            throw new ApiException("Gemini API key is not configured.", HttpStatus.SERVICE_UNAVAILABLE, "AI_KEY_MISSING");
        }

        String url = String.format("%s/%s:generateContent?key=%s",
                geminiProperties.getApiUrl(),
                geminiProperties.getModel(),
                apiKey
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0.2
                )
        );

        log.info("Dispatching prompt to Gemini model: {}", geminiProperties.getModel());

        try {
            String rawResponse = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(geminiProperties.getTimeoutSeconds()))
                    .block();

            if (rawResponse == null) {
                throw new ApiException("Received empty response from Gemini API", HttpStatus.BAD_GATEWAY, "AI_EMPTY_RESPONSE");
            }

            JsonNode rootNode = objectMapper.readTree(rawResponse);
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    return parts.get(0).path("text").asText();
                }
            }

            throw new ApiException("Unexpected Gemini API response structure", HttpStatus.BAD_GATEWAY, "AI_PARSE_ERROR");
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error communicating with Gemini API: {}", e.getMessage());
            throw new ApiException("Failed to communicate with AI analysis service: " + e.getMessage(),
                    HttpStatus.BAD_GATEWAY, "AI_CALL_FAILED");
        }
    }
}
