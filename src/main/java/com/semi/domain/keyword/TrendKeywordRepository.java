package com.semi.domain.keyword;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendKeywordRepository extends JpaRepository<TrendKeyword, Long> {

    /** 현재 활성 키워드 전체 조회 (대시보드용) */
    List<TrendKeyword> findByIsActiveTrueOrderByRankAsc();

    List<TrendKeyword> findByIsActiveTrueOrderByIdAsc();

    @Query(value = """
            SELECT *
            FROM trend_keyword
            WHERE collected_at >= :start
              AND collected_at < :end
            ORDER BY id ASC
            LIMIT 20
            """, nativeQuery = true)
    List<TrendKeyword> findTop20KeywordsCollectedBetweenOrderById(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
