package com.devtools.ai.parser;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@Builder
public class ProcessedErrorInput {
    private final String originalText;
    private final String sanitizedText;
    private final String normalizedText;
    private final String detectedException;
    private final String rootCauseMessage;
    private final List<String> causedByChain;
    private final boolean wasSanitized;
    private final Set<String> redactedCategories;
    private final boolean wasTruncated;
    private final int originalLength;
    private final int processedLength;
}
