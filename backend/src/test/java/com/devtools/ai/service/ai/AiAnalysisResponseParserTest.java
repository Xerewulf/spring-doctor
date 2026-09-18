package com.devtools.ai.service.ai;

import com.devtools.ai.dto.DiagnosisDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiAnalysisResponseParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldParseStructuredDiagnosisJson() throws Exception {
        String json = """
                {
                  "summary": "Hibernate Session was closed when accessing lazy collection",
                  "rootCause": "Access to Customer.orders proxy outside transactional boundary",
                  "confidence": "HIGH",
                  "severity": "MEDIUM",
                  "whyItHappens": "Lazy proxies require open persistence context",
                  "suggestedFixes": [
                    {
                      "title": "Use EntityGraph",
                      "description": "Fetch eager via @EntityGraph",
                      "code": "@EntityGraph(attributePaths = {\\"orders\\\"})",
                      "language": "java"
                    }
                  ],
                  "thingsToCheck": [
                    "Verify transactional context"
                  ],
                  "relatedTechnologies": [
                    "Spring Boot",
                    "Hibernate"
                  ],
                  "possibleCauses": [
                    "Direct JSON serialization in controller"
                  ]
                }
                """;

        DiagnosisDto dto = objectMapper.readValue(json, DiagnosisDto.class);

        assertNotNull(dto);
        assertEquals("HIGH", dto.getConfidence());
        assertEquals("MEDIUM", dto.getSeverity());
        assertEquals(1, dto.getSuggestedFixes().size());
        assertEquals("Use EntityGraph", dto.getSuggestedFixes().get(0).getTitle());
        assertEquals(2, dto.getRelatedTechnologies().size());
    }
}
