package com.devtools.ai.service;

import com.devtools.ai.entity.Subscription;
import com.devtools.ai.entity.User;
import com.devtools.ai.exception.ApiException;
import com.devtools.ai.repository.SubscriptionRepository;
import com.devtools.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    private static final Set<String> VALID_PLANS = Set.of("FREE", "PRO", "TEAM");

    @Transactional(readOnly = true)
    public Map<String, Object> getSubscriptionDetails(User user) {
        Subscription subscription = subscriptionRepository.findByUserId(user.getId())
                .orElseGet(() -> Subscription.builder()
                        .user(user)
                        .plan(user.getPlan() != null ? user.getPlan() : "FREE")
                        .status("ACTIVE")
                        .build());

        return Map.of(
                "plan", subscription.getPlan(),
                "status", subscription.getStatus(),
                "currentPeriodEnd", subscription.getCurrentPeriodEnd() != null ? subscription.getCurrentPeriodEnd() : "N/A",
                "stripeCustomerId", "stripe_customer_pending"
        );
    }

    @Transactional
    public Map<String, Object> updatePlan(User user, String newPlan) {
        String planUpper = newPlan != null ? newPlan.toUpperCase().trim() : "FREE";
        if (!VALID_PLANS.contains(planUpper)) {
            throw new ApiException("Invalid subscription plan: " + newPlan, HttpStatus.BAD_REQUEST, "INVALID_PLAN");
        }

        user.setPlan(planUpper);
        userRepository.save(user);

        Subscription subscription = subscriptionRepository.findByUserId(user.getId())
                .orElseGet(() -> Subscription.builder().user(user).build());

        subscription.setPlan(planUpper);
        subscription.setStatus("ACTIVE");
        subscription.setCurrentPeriodEnd(LocalDateTime.now().plusDays(30));
        subscriptionRepository.save(subscription);

        log.info("User {} upgraded plan to {}", user.getEmail(), planUpper);

        return Map.of(
                "success", true,
                "message", "Successfully upgraded to plan " + planUpper,
                "plan", planUpper,
                "status", "ACTIVE"
        );
    }

    public Map<String, Object> createCheckoutSession(User user, String targetPlan) {
        // Architecture ready for Stripe Checkout session creation
        log.info("Simulating Stripe Checkout session initiation for plan {} by user {}", targetPlan, user.getEmail());
        return Map.of(
                "checkoutUrl", "/pricing?status=simulated_success&plan=" + targetPlan,
                "targetPlan", targetPlan,
                "mode", "MOCK_STRIPE_CHECKOUT"
        );
    }
}
