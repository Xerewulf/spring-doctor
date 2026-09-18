package com.devtools.ai.controller;

import com.devtools.ai.dto.ApiResponse;
import com.devtools.ai.entity.User;
import com.devtools.ai.exception.ApiException;
import com.devtools.ai.service.AuthService;
import com.devtools.ai.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;
    private final AuthService authService;

    @GetMapping("/subscription")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSubscription() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(billingService.getSubscriptionDetails(user)));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<ApiResponse<Map<String, Object>>> upgradePlan(@RequestBody Map<String, String> body) {
        User user = getAuthenticatedUser();
        String plan = body.get("plan");
        return ResponseEntity.ok(ApiResponse.ok(billingService.updatePlan(user, plan)));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCheckout(@RequestBody Map<String, String> body) {
        User user = getAuthenticatedUser();
        String plan = body.getOrDefault("plan", "PRO");
        return ResponseEntity.ok(ApiResponse.ok(billingService.createCheckoutSession(user, plan)));
    }

    private User getAuthenticatedUser() {
        User user = authService.getCurrentUserOrNull();
        if (user == null) {
            throw new ApiException("Authentication required", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        return user;
    }
}
