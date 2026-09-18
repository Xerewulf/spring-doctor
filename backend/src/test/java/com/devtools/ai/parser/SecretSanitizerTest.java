package com.devtools.ai.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecretSanitizerTest {

    private SecretSanitizer sanitizer;

    @BeforeEach
    void setUp() {
        sanitizer = new SecretSanitizer();
    }

    @Test
    void shouldRedactBearerToken() {
        String input = "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        SecretSanitizer.SanitizationResult result = sanitizer.sanitize(input);

        assertTrue(result.isRedacted());
        assertTrue(result.getRedactedCategories().contains("Bearer Token"));
        assertFalse(result.getSanitizedText().contains("SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"));
        assertTrue(result.getSanitizedText().contains("Bearer [REDACTED]"));
    }

    @Test
    void shouldRedactJdbcCredentials() {
        String input = "Failed to connect to jdbc:postgresql://dbadmin:p@ssw0rd123@db.prod.internal:5432/main_db";
        SecretSanitizer.SanitizationResult result = sanitizer.sanitize(input);

        assertTrue(result.isRedacted());
        assertFalse(result.getSanitizedText().contains("p@ssw0rd123"));
        assertTrue(result.getSanitizedText().contains("jdbc:postgresql://dbadmin:[REDACTED]@db.prod.internal:5432/main_db"));
    }

    @Test
    void shouldRedactPasswordKeyValue() {
        String input = "spring.datasource.password=MySecretPassword999\napi_key: ak_live_9876543210abcdef";
        SecretSanitizer.SanitizationResult result = sanitizer.sanitize(input);

        assertTrue(result.isRedacted());
        assertFalse(result.getSanitizedText().contains("MySecretPassword999"));
        assertFalse(result.getSanitizedText().contains("ak_live_9876543210abcdef"));
        assertTrue(result.getSanitizedText().contains("[REDACTED]"));
    }

    @Test
    void shouldLeaveCleanTraceUntouched() {
        String input = "java.lang.NullPointerException: Cannot invoke method on null object\n\tat com.example.service.UserService.getUser(UserService.java:25)";
        SecretSanitizer.SanitizationResult result = sanitizer.sanitize(input);

        assertFalse(result.isRedacted());
        assertEquals(input, result.getSanitizedText());
    }
}
