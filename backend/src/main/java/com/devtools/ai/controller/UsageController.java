package com.devtools.ai.controller;

import com.devtools.ai.dto.ApiResponse;
import com.devtools.ai.dto.UsageDto;
import com.devtools.ai.entity.User;
import com.devtools.ai.service.AuthService;
import com.devtools.ai.service.UsageTrackingService;
import com.devtools.ai.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageTrackingService usageTrackingService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<UsageDto>> getUsage(HttpServletRequest request) {
        String clientIp = IpUtils.extractClientIp(request);
        User currentUser = authService.getCurrentUserOrNull();

        UsageDto usage = usageTrackingService.getUsageSummary(currentUser, clientIp);
        return ResponseEntity.ok(ApiResponse.ok(usage));
    }
}
