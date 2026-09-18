package com.devtools.ai.service;

import com.devtools.ai.dto.AnalysisDetailDto;
import com.devtools.ai.dto.AnalysisSummaryDto;
import com.devtools.ai.dto.DashboardStatsDto;
import com.devtools.ai.dto.DiagnosisDto;
import com.devtools.ai.dto.UsageDto;
import com.devtools.ai.entity.Analysis;
import com.devtools.ai.entity.User;
import com.devtools.ai.exception.ResourceNotFoundException;
import com.devtools.ai.repository.AnalysisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisHistoryService {

    private final AnalysisRepository analysisRepository;
    private final UsageTrackingService usageTrackingService;
    private final ObjectMapper objectMapper;

    @Transactional
    public Analysis saveAnalysis(User user, String title, String errorType, String technology,
                                 String summary, String rawRedactedText, DiagnosisDto diagnosis, boolean saveInput) {
        String diagnosisJson;
        try {
            diagnosisJson = objectMapper.writeValueAsString(diagnosis);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize diagnosis to JSON", e);
            diagnosisJson = "{}";
        }

        Analysis analysis = Analysis.builder()
                .user(user)
                .title(title != null && !title.isBlank() ? title : "Analysis of " + (errorType != null ? errorType : "Error"))
                .errorType(errorType)
                .technology(technology)
                .summary(summary)
                .rawErrorTextRedacted(saveInput ? rawRedactedText : null)
                .diagnosisJson(diagnosisJson)
                .isSavedInput(saveInput)
                .build();

        return analysisRepository.save(analysis);
    }

    @Transactional(readOnly = true)
    public Page<AnalysisSummaryDto> getUserHistory(User user, Pageable pageable) {
        return analysisRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::mapToSummaryDto);
    }

    @Transactional(readOnly = true)
    public AnalysisDetailDto getAnalysisById(Long id, User user) {
        Analysis analysis = analysisRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found with ID: " + id));

        DiagnosisDto diagnosisDto;
        try {
            diagnosisDto = objectMapper.readValue(analysis.getDiagnosisJson(), DiagnosisDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize stored diagnosis JSON", e);
            diagnosisDto = DiagnosisDto.builder()
                    .summary(analysis.getSummary())
                    .rootCause("Unable to parse full diagnosis JSON")
                    .build();
        }

        return AnalysisDetailDto.builder()
                .id(analysis.getId())
                .title(analysis.getTitle())
                .errorType(analysis.getErrorType())
                .technology(analysis.getTechnology())
                .summary(analysis.getSummary())
                .rawErrorTextRedacted(analysis.getRawErrorTextRedacted())
                .diagnosis(diagnosisDto)
                .isSavedInput(Boolean.TRUE.equals(analysis.getIsSavedInput()))
                .createdAt(analysis.getCreatedAt())
                .updatedAt(analysis.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteAnalysis(Long id, User user) {
        Analysis analysis = analysisRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found with ID: " + id));
        analysisRepository.delete(analysis);
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats(User user, String clientIp) {
        long totalCount = analysisRepository.countByUserId(user.getId());
        UsageDto usage = usageTrackingService.getUsageSummary(user, clientIp);

        List<Analysis> top10 = analysisRepository.findTop10ByUserIdOrderByCreatedAtDesc(user.getId());
        List<AnalysisSummaryDto> recent = top10.stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());

        // Compute most common error and tech
        String mostCommonError = top10.stream()
                .map(Analysis::getErrorType)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None yet");

        String mostCommonTechnology = top10.stream()
                .map(Analysis::getTechnology)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Spring Boot");

        return DashboardStatsDto.builder()
                .totalAnalyses(totalCount)
                .usedToday(usage.getUsedToday())
                .dailyLimit(usage.getDailyLimit())
                .remainingToday(usage.getRemainingToday())
                .plan(user.getPlan())
                .mostCommonError(mostCommonError)
                .mostCommonTechnology(mostCommonTechnology)
                .recentAnalyses(recent)
                .build();
    }

    private AnalysisSummaryDto mapToSummaryDto(Analysis analysis) {
        return AnalysisSummaryDto.builder()
                .id(analysis.getId())
                .title(analysis.getTitle())
                .errorType(analysis.getErrorType())
                .technology(analysis.getTechnology())
                .summary(analysis.getSummary())
                .isSavedInput(Boolean.TRUE.equals(analysis.getIsSavedInput()))
                .createdAt(analysis.getCreatedAt())
                .build();
    }
}
