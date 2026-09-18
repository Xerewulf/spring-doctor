package com.devtools.ai.service;

import com.devtools.ai.entity.UsageRecord;
import com.devtools.ai.entity.User;
import com.devtools.ai.exception.UsageLimitExceededException;
import com.devtools.ai.repository.UsageRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsageLimitTest {

    @Mock
    private UsageRecordRepository usageRecordRepository;

    @InjectMocks
    private UsageTrackingService usageTrackingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(usageTrackingService, "anonymousDailyLimit", 3);
        ReflectionTestUtils.setField(usageTrackingService, "freeDailyLimit", 10);
        ReflectionTestUtils.setField(usageTrackingService, "proDailyLimit", 250);
    }

    @Test
    void shouldAllowAnonymousWhenUnderLimit() {
        when(usageRecordRepository.findByClientIpHashAndRequestDateAndUserIdIsNull(any(), eq(LocalDate.now())))
                .thenReturn(Optional.of(UsageRecord.builder().analysisCount(2).build()));

        assertDoesNotThrow(() -> usageTrackingService.validateUsageLimit(null, "192.168.1.1"));
    }

    @Test
    void shouldBlockAnonymousWhenAtLimit() {
        when(usageRecordRepository.findByClientIpHashAndRequestDateAndUserIdIsNull(any(), eq(LocalDate.now())))
                .thenReturn(Optional.of(UsageRecord.builder().analysisCount(3).build()));

        assertThrows(UsageLimitExceededException.class, () ->
                usageTrackingService.validateUsageLimit(null, "192.168.1.1"));
    }

    @Test
    void shouldAllowFreeUserUpTo10() {
        User user = User.builder().id(42L).plan("FREE").build();
        when(usageRecordRepository.findByUserIdAndRequestDate(eq(42L), eq(LocalDate.now())))
                .thenReturn(Optional.of(UsageRecord.builder().analysisCount(9).build()));

        assertDoesNotThrow(() -> usageTrackingService.validateUsageLimit(user, "192.168.1.1"));
    }

    @Test
    void shouldBlockFreeUserAt10() {
        User user = User.builder().id(42L).plan("FREE").build();
        when(usageRecordRepository.findByUserIdAndRequestDate(eq(42L), eq(LocalDate.now())))
                .thenReturn(Optional.of(UsageRecord.builder().analysisCount(10).build()));

        assertThrows(UsageLimitExceededException.class, () ->
                usageTrackingService.validateUsageLimit(user, "192.168.1.1"));
    }
}
