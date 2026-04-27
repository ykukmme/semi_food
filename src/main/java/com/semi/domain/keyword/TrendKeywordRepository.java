package com.semi.domain.keyword;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendKeywordRepository extends JpaRepository<TrendKeyword, Long> {

    /** 현재 활성 키워드 전체 조회 (대시보드용) */
    List<TrendKeyword> findByIsActiveTrueOrderByRankAsc();

    // 서버에서 ID를 수동처리하기 위한 코드
    @Query("SELECT MAX(t.id) FROM TrendKeyword t")
    Long findMaxId();
    // 서버에서 ID를 수동처리하기 위한 코드
    // 중복 체크용 (데이터가 이미 있는지 확인)
    boolean existsByCollectedAtAndKeyword(LocalDateTime collectedAt, String keyword);

    TrendKeyword findFirstByOrderByIdDesc();
}
