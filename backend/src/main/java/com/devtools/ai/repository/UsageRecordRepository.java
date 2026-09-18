package com.devtools.ai.repository;

import com.devtools.ai.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {
    Optional<UsageRecord> findByUserIdAndRequestDate(Long userId, LocalDate requestDate);
    Optional<UsageRecord> findByClientIpHashAndRequestDateAndUserIdIsNull(String clientIpHash, LocalDate requestDate);
}
