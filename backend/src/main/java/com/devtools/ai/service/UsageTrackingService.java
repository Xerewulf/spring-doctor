package com.devtools.ai.service;

import com.devtools.ai.dto.UsageDto;
import com.devtools.ai.entity.UsageRecord;
import com.devtools.ai.entity.User;
import com.devtools.ai.exception.UsageLimitExceededException;
import com.devtools.ai.repository.UsageRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageTrackingService {

    private final UsageRecordRepository usageRecordRepository;

    @Value("${devtools.limits.anonymous-daily-limit:3}")
    private int anonymousDailyLimit;

    @Value("${devtools.limits.free-daily-limit:10}")
    private int freeDailyLimit;

    @Value("${devtools.limits.pro-daily-limit:250}")
    private int proDailyLimit;

    @Transactional(readOnly = true)
    public void validateUsageLimit(User user, String clientIp) {
        LocalDate today = LocalDate.now();
        int limit = getDailyLimitFor(user);
        int currentUsage = getCurrentDailyUsage(user, clientIp, today);

        if (currentUsage >= limit) {
            String plan = user != null ? user.getPlan() : "ANONYMOUS";
            String upgradeMessage = user == null
                    ? String.format("Daily anonymous limit reached (%d/%d). Sign up for a free account to get 10 analyses per day.", currentUsage, limit)
                    : String.format("Daily limit reached for %s plan (%d/%d). Upgrade to Pro for unlimited analyses.", plan, currentUsage, limit);

            throw new UsageLimitExceededException(upgradeMessage);
        }
    }

    @Transactional
    public void recordUsage(User user, String clientIp) {
        LocalDate today = LocalDate.now();
        String ipHash = hashIp(clientIp);

        if (user != null) {
            UsageRecord record = usageRecordRepository.findByUserIdAndRequestDate(user.getId(), today)
                    .orElseGet(() -> UsageRecord.builder()
                            .user(user)
                            .clientIpHash(ipHash)
                            .requestDate(today)
                            .analysisCount(0)
                            .requestType("ERROR_ANALYSIS")
                            .build());

            record.setAnalysisCount(record.getAnalysisCount() + 1);
            usageRecordRepository.save(record);
        } else {
            UsageRecord record = usageRecordRepository.findByClientIpHashAndRequestDateAndUserIdIsNull(ipHash, today)
                    .orElseGet(() -> UsageRecord.builder()
                            .user(null)
                            .clientIpHash(ipHash)
                            .requestDate(today)
                            .analysisCount(0)
                            .requestType("ERROR_ANALYSIS")
                            .build());

            record.setAnalysisCount(record.getAnalysisCount() + 1);
            usageRecordRepository.save(record);
        }
    }

    @Transactional(readOnly = true)
    public UsageDto getUsageSummary(User user, String clientIp) {
        LocalDate today = LocalDate.now();
        int limit = getDailyLimitFor(user);
        int currentUsage = getCurrentDailyUsage(user, clientIp, today);
        int remaining = Math.max(0, limit - currentUsage);

        String plan = user != null ? user.getPlan() : "ANONYMOUS";

        return UsageDto.builder()
                .plan(plan)
                .dailyLimit(limit)
                .usedToday(currentUsage)
                .remainingToday(remaining)
                .authenticated(user != null)
                .build();
    }

    public int getDailyLimitFor(User user) {
        if (user == null) {
            return anonymousDailyLimit;
        }
        if ("PRO".equalsIgnoreCase(user.getPlan()) || "TEAM".equalsIgnoreCase(user.getPlan())) {
            return proDailyLimit;
        }
        return freeDailyLimit;
    }

    private int getCurrentDailyUsage(User user, String clientIp, LocalDate date) {
        if (user != null) {
            return usageRecordRepository.findByUserIdAndRequestDate(user.getId(), date)
                    .map(UsageRecord::getAnalysisCount)
                    .orElse(0);
        } else {
            String ipHash = hashIp(clientIp);
            return usageRecordRepository.findByClientIpHashAndRequestDateAndUserIdIsNull(ipHash, date)
                    .map(UsageRecord::getAnalysisCount)
                    .orElse(0);
        }
    }

    public String hashIp(String clientIp) {
        if (clientIp == null || clientIp.isBlank()) {
            return "unknown_ip";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(("salt_devtools_" + clientIp).getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(clientIp.hashCode());
        }
    }
}
