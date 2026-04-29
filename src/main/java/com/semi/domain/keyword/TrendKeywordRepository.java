package com.semi.domain.keyword;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendKeywordRepository extends JpaRepository<TrendKeyword, Long> {

    /** 현재 활성 키워드 전체 조회 (대시보드용) */
    List<TrendKeyword> findByIsActiveTrueOrderByRankAsc();

    List<TrendKeyword> findByIsActiveTrueOrderByIdAsc();

    List<TrendKeyword> findTop20ByCollectedAtGreaterThanEqualAndCollectedAtLessThanOrderByIdAsc(
            LocalDateTime start,
            LocalDateTime end
    );
}
