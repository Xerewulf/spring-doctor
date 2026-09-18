package com.devtools.ai.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorParser {

    private final SecretSanitizer secretSanitizer;

    private static final int MAX_PROCESSED_LENGTH = 16000;

    // Supported common exceptions
    private static final List<String> KNOWN_EXCEPTIONS = List.of(
            "LazyInitializationException",
            "BeanCreationException",
            "UnsatisfiedDependencyException",
            "NullPointerException",
            "DataAccessException",
            "DataIntegrityViolationException",
            "ConstraintViolationException",
            "PSQLException",
            "SQLException",
            "MethodArgumentNotValidException",
            "HttpMessageNotReadableException",
            "NoSuchBeanDefinitionException",
            "NoUniqueBeanDefinitionException",
            "AccessDeniedException",
            "AuthenticationException",
            "FeignException",
            "RestClientException",
            "TimeoutException",
            "OutOfMemoryError",
            "StackOverflowError",
            "InvalidDataAccessResourceUsageException",
            "TransactionSystemException"
    );

    private static final Pattern EXCEPTION_CLASS_PATTERN = Pattern.compile(
            "([a-zA-Z0-9_.]+(?:Exception|Error)):?\\s*(.*)"
    );

    private static final Pattern CAUSED_BY_PATTERN = Pattern.compile(
            "Caused by:\\s+([a-zA-Z0-9_.]+(?:Exception|Error))?:?\\s*(.*)",
            Pattern.MULTILINE
    );

    public ProcessedErrorInput process(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return ProcessedErrorInput.builder()
                    .originalText("")
                    .sanitizedText("")
                    .normalizedText("")
                    .detectedException("Unknown")
                    .rootCauseMessage("")
                    .causedByChain(Collections.emptyList())
                    .wasSanitized(false)
                    .redactedCategories(Collections.emptySet())
                    .wasTruncated(false)
                    .originalLength(0)
                    .processedLength(0)
                    .build();
        }

        // 1. Sanitize sensitive information
        SecretSanitizer.SanitizationResult sanitizationResult = secretSanitizer.sanitize(rawInput);
        String sanitizedText = sanitizationResult.getSanitizedText();

        // 2. Detect exception type and root causes
        String detectedException = detectException(sanitizedText);
        List<String> causedByChain = extractCausedByChain(sanitizedText);
        String rootCauseMessage = extractRootCauseMessage(sanitizedText, causedByChain);

        // 3. Normalize & condense stack trace if overly large
        boolean wasTruncated = false;
        String normalizedText = sanitizedText;
        if (sanitizedText.length() > MAX_PROCESSED_LENGTH) {
            normalizedText = condenseStackTrace(sanitizedText, MAX_PROCESSED_LENGTH);
            wasTruncated = true;
        }

        return ProcessedErrorInput.builder()
                .originalText(rawInput)
                .sanitizedText(sanitizedText)
                .normalizedText(normalizedText)
                .detectedException(detectedException)
                .rootCauseMessage(rootCauseMessage)
                .causedByChain(causedByChain)
                .wasSanitized(sanitizationResult.isRedacted())
                .redactedCategories(sanitizationResult.getRedactedCategories())
                .wasTruncated(wasTruncated)
                .originalLength(rawInput.length())
                .processedLength(normalizedText.length())
                .build();
    }

    public String detectException(String text) {
        // First, check explicit known list
        for (String known : KNOWN_EXCEPTIONS) {
            if (text.contains(known)) {
                return known;
            }
        }

        // Fallback to regex match on Exception or Error
        Matcher matcher = EXCEPTION_CLASS_PATTERN.matcher(text);
        if (matcher.find()) {
            String fullClass = matcher.group(1);
            int lastDot = fullClass.lastIndexOf('.');
            return (lastDot >= 0 && lastDot < fullClass.length() - 1)
                    ? fullClass.substring(lastDot + 1)
                    : fullClass;
        }

        return "JavaException";
    }

    public List<String> extractCausedByChain(String text) {
        List<String> chain = new ArrayList<>();
        Matcher matcher = CAUSED_BY_PATTERN.matcher(text);
        while (matcher.find()) {
            String exception = matcher.group(1);
            String message = matcher.group(2);
            if (exception != null && !exception.isBlank()) {
                chain.add(exception.trim() + (message != null && !message.isBlank() ? ": " + message.trim() : ""));
            } else if (message != null && !message.isBlank()) {
                chain.add(message.trim());
            }
        }
        return chain;
    }

    private String extractRootCauseMessage(String text, List<String> causedByChain) {
        if (!causedByChain.isEmpty()) {
            return causedByChain.get(causedByChain.size() - 1);
        }
        String[] lines = text.split("\r?\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.contains("Exception") || trimmed.contains("Error")) {
                return trimmed;
            }
        }
        return "";
    }

    public String condenseStackTrace(String text, int maxLength) {
        String[] lines = text.split("\r?\n");
        if (lines.length <= 100 && text.length() <= maxLength) {
            return text;
        }

        StringBuilder condensed = new StringBuilder();
        int omittedFrames = 0;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();

            // Keep header, Caused by, root cause lines, and application frames
            boolean isImportant = i < 15
                    || trimmed.startsWith("Caused by:")
                    || trimmed.startsWith("org.springframework.boot")
                    || trimmed.contains("Exception:")
                    || trimmed.contains("Error:")
                    || !trimmed.startsWith("at ");

            // If it's a deep standard internal frame, compress
            boolean isInternalSpringOrJvm = trimmed.startsWith("at java.base/")
                    || trimmed.startsWith("at org.apache.catalina.")
                    || trimmed.startsWith("at org.apache.tomcat.")
                    || trimmed.startsWith("at org.springframework.web.filter.");

            if (isImportant || (!isInternalSpringOrJvm && condensed.length() < maxLength - 1000)) {
                if (omittedFrames > 0) {
                    condensed.append("\t... ").append(omittedFrames).append(" internal framework calls omitted ...\n");
                    omittedFrames = 0;
                }
                condensed.append(line).append("\n");
            } else {
                omittedFrames++;
            }

            if (condensed.length() >= maxLength - 500) {
                condensed.append("\n[... Stack trace condensed for analysis. Showing root causes above ...]\n");
                break;
            }
        }

        if (omittedFrames > 0) {
            condensed.append("\t... ").append(omittedFrames).append(" internal framework calls omitted ...\n");
        }

        return condensed.toString();
    }
}
