package com.devtools.ai.controller;

import com.devtools.ai.dto.ApiResponse;
import com.devtools.ai.dto.DashboardStatsDto;
import com.devtools.ai.entity.User;
import com.devtools.ai.exception.ApiException;
import com.devtools.ai.service.AnalysisHistoryService;
import com.devtools.ai.service.AuthService;
import com.devtools.ai.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AnalysisHistoryService analysisHistoryService;
    private final AuthService authService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats(HttpServletRequest request) {
        User user = authService.getCurrentUserOrNull();
        if (user == null) {
            throw new ApiException("Authentication required to access dashboard", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }

        String clientIp = IpUtils.extractClientIp(request);
        DashboardStatsDto stats = analysisHistoryService.getDashboardStats(user, clientIp);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
