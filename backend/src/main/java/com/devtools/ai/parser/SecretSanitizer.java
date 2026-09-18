package com.devtools.ai.parser;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SecretSanitizer {

    private static final Pattern BEARER_PATTERN = Pattern.compile(
            "Bearer\\s+[a-zA-Z0-9\\-_=]+\\.[a-zA-Z0-9\\-_=]+\\.[a-zA-Z0-9\\-_=]+",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern JWT_RAW_PATTERN = Pattern.compile(
            "eyJ[a-zA-Z0-9_-]{10,}\\.[a-zA-Z0-9_-]{10,}\\.[a-zA-Z0-9_-]+"
    );

    private static final Pattern JDBC_CREDENTIALS_PATTERN = Pattern.compile(
            "(jdbc:[a-zA-Z0-9]+://)([^:/\\s]+):([^\\s]+?)@([a-zA-Z0-9_.-]+(?::[0-9]+)?[/\\?])",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern KEY_VALUE_SECRET_PATTERN = Pattern.compile(
            "(?i)\\b(password|passwd|pwd|secret|api[_-]?key|client[_-]?secret|private[_-]?key)\\s*[:=]\\s*([\"']?[^\"'\\s,;]{4,}[\"']?)"
    );

    private static final Pattern AWS_KEY_PATTERN = Pattern.compile(
            "\\b(AKIA[0-9A-Z]{16})\\b"
    );

    @Getter
    public static class SanitizationResult {
        private final String sanitizedText;
        private final boolean redacted;
        private final Set<String> redactedCategories;

        public SanitizationResult(String sanitizedText, boolean redacted, Set<String> redactedCategories) {
            this.sanitizedText = sanitizedText;
            this.redacted = redacted;
            this.redactedCategories = redactedCategories;
        }
    }

    public SanitizationResult sanitize(String input) {
        if (input == null || input.isBlank()) {
            return new SanitizationResult("", false, Set.of());
        }

        String result = input;
        boolean modified = false;
        Set<String> categories = new HashSet<>();

        // 1. Redact Authorization: Bearer <jwt>
        Matcher bearerMatcher = BEARER_PATTERN.matcher(result);
        if (bearerMatcher.find()) {
            result = bearerMatcher.replaceAll("Bearer [REDACTED]");
            modified = true;
            categories.add("Bearer Token");
        }

        // 2. Redact standalone JWT tokens
        Matcher jwtMatcher = JWT_RAW_PATTERN.matcher(result);
        if (jwtMatcher.find()) {
            result = jwtMatcher.replaceAll("[REDACTED_JWT]");
            modified = true;
            categories.add("JWT Token");
        }

        // 3. Redact JDBC database credentials
        Matcher jdbcMatcher = JDBC_CREDENTIALS_PATTERN.matcher(result);
        if (jdbcMatcher.find()) {
            result = jdbcMatcher.replaceAll("$1$2:[REDACTED]@$4");
            modified = true;
            categories.add("Database Credentials");
        }

        // 4. Redact key-value secrets
        Matcher kvMatcher = KEY_VALUE_SECRET_PATTERN.matcher(result);
        if (kvMatcher.find()) {
            result = kvMatcher.replaceAll("$1: [REDACTED]");
            modified = true;
            categories.add("Credential / Secret");
        }

        // 5. Redact AWS Access Keys
        Matcher awsMatcher = AWS_KEY_PATTERN.matcher(result);
        if (awsMatcher.find()) {
            result = awsMatcher.replaceAll("[REDACTED_AWS_KEY]");
            modified = true;
            categories.add("Cloud Access Key");
        }

        return new SanitizationResult(result, modified, categories);
    }
}
