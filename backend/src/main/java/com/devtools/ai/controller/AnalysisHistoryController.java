package com.devtools.ai.controller;

import com.devtools.ai.dto.AnalysisDetailDto;
import com.devtools.ai.dto.AnalysisSummaryDto;
import com.devtools.ai.dto.ApiResponse;
import com.devtools.ai.entity.User;
import com.devtools.ai.exception.ApiException;
import com.devtools.ai.service.AnalysisHistoryService;
import com.devtools.ai.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisHistoryController {

    private final AnalysisHistoryService analysisHistoryService;
    private final AuthService authService;

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<AnalysisSummaryDto>>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        User user = getAuthenticatedUser();
        Page<AnalysisSummaryDto> history = analysisHistoryService.getUserHistory(user, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.ok(history));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnalysisDetailDto>> getAnalysisDetail(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        AnalysisDetailDto detail = analysisHistoryService.getAnalysisById(id, user);
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAnalysis(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        analysisHistoryService.deleteAnalysis(id, user);
        return ResponseEntity.ok(ApiResponse.ok(null, "Analysis record deleted successfully"));
    }

    private User getAuthenticatedUser() {
        User user = authService.getCurrentUserOrNull();
        if (user == null) {
            throw new ApiException("Authentication required to access history", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        return user;
    }
}
