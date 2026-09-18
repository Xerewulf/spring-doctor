package com.devtools.ai.service.ai;

import com.devtools.ai.parser.ProcessedErrorInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiPromptService {

    private final ErrorAnalysisPromptBuilder promptBuilder;

    public String generateErrorAnalysisPrompt(ProcessedErrorInput input, String technology, String context) {
        return promptBuilder.buildPrompt(input, technology, context);
    }
}
