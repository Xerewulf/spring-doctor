package com.devtools.ai.service.ai;

import com.devtools.ai.dto.DiagnosisDto;
import com.devtools.ai.parser.ProcessedErrorInput;

public interface AIAnalysisService {
    DiagnosisDto analyzeError(ProcessedErrorInput processedInput, String technology, String context);
}
