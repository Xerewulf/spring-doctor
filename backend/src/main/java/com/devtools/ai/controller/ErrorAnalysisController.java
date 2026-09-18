package com.devtools.ai.controller;

import com.devtools.ai.dto.ApiResponse;
import com.devtools.ai.dto.DiagnosisDto;
import com.devtools.ai.dto.ErrorAnalysisRequest;
import com.devtools.ai.entity.User;
import com.devtools.ai.service.AuthService;
import com.devtools.ai.service.ErrorAnalysisService;
import com.devtools.ai.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class ErrorAnalysisController {

    private final ErrorAnalysisService errorAnalysisService;
    private final AuthService authService;

    @PostMapping("/error")
    public ResponseEntity<ApiResponse<DiagnosisDto>> analyzeError(
            @Valid @RequestBody ErrorAnalysisRequest request,
            HttpServletRequest servletRequest
    ) {
        String clientIp = IpUtils.extractClientIp(servletRequest);
        User currentUser = authService.getCurrentUserOrNull();

        log.info("Incoming error analysis request from IP={} User={}",
                clientIp, currentUser != null ? currentUser.getEmail() : "anonymous");

        DiagnosisDto diagnosis = errorAnalysisService.analyze(request, currentUser, clientIp);
        return ResponseEntity.ok(ApiResponse.ok(diagnosis, "Error successfully analyzed"));
    }
}
