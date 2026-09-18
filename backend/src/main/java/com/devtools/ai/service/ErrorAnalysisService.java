package com.devtools.ai.service;

import com.devtools.ai.dto.DiagnosisDto;
import com.devtools.ai.dto.ErrorAnalysisRequest;
import com.devtools.ai.entity.Analysis;
import com.devtools.ai.entity.User;
import com.devtools.ai.exception.ApiException;
import com.devtools.ai.parser.ErrorParser;
import com.devtools.ai.parser.ProcessedErrorInput;
import com.devtools.ai.service.ai.AIAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorAnalysisService {

    private final ErrorParser errorParser;
    private final AIAnalysisService aiAnalysisService;
    private final UsageTrackingService usageTrackingService;
    private final AnalysisHistoryService analysisHistoryService;

    @Value("${devtools.limits.max-error-input-characters:32000}")
    private int maxInputLength;

    public DiagnosisDto analyze(ErrorAnalysisRequest request, User user, String clientIp) {
        // 1. Basic validation
        if (request.getErrorText() == null || request.getErrorText().trim().isEmpty()) {
            throw new ApiException("Error text cannot be empty", HttpStatus.BAD_REQUEST, "EMPTY_INPUT");
        }

        if (request.getErrorText().length() > maxInputLength) {
            throw new ApiException(
                    String.format("Input length (%d characters) exceeds allowed limit of %d characters",
                            request.getErrorText().length(), maxInputLength),
                    HttpStatus.BAD_REQUEST,
                    "INPUT_TOO_LARGE"
            );
        }

        // 2. Validate usage limits (throws UsageLimitExceededException if exceeded)
        usageTrackingService.validateUsageLimit(user, clientIp);

        // 3. Process, sanitize, and normalize stack trace
        ProcessedErrorInput processedInput = errorParser.process(request.getErrorText());

        log.info("Analyzing error: detectedException={}, sanitized={}, length={}",
                processedInput.getDetectedException(),
                processedInput.isWasSanitized(),
                processedInput.getProcessedLength()
        );

        // 4. Invoke AI analysis (Gemini or fallback)
        String technology = request.getTechnology() != null ? request.getTechnology() : "SPRING_BOOT";
        DiagnosisDto diagnosis = aiAnalysisService.analyzeError(processedInput, technology, request.getContext());

        // 5. Deduct / record usage
        usageTrackingService.recordUsage(user, clientIp);

        // 6. Save to history if user is authenticated
        if (user != null) {
            boolean saveInput = Boolean.TRUE.equals(request.getSaveInput());
            Analysis saved = analysisHistoryService.saveAnalysis(
                    user,
                    "Analysis: " + diagnosis.getDetectedException(),
                    diagnosis.getDetectedException(),
                    technology,
                    diagnosis.getSummary(),
                    processedInput.getSanitizedText(),
                    diagnosis,
                    saveInput
            );
            diagnosis.setAnalysisId(saved.getId());
            diagnosis.setWasInputSaved(saveInput);
        }

        return diagnosis;
    }
}
