package com.devtools.ai.repository;

import com.devtools.ai.entity.Analysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Page<Analysis> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Analysis> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Analysis> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
}
