package com.semi.domain.keyword;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendKeywordRepository extends JpaRepository<TrendKeyword, Long> {

    /** 현재 활성 키워드 전체 조회 (대시보드용) */
    List<TrendKeyword> findByIsActiveTrueOrderByRankAsc();

    // 서버에서 ID를 수동처리하기 위한 코드
    @Query("SELECT MAX(t.id) FROM TrendKeyword t")
    Long findMaxId();

    /** 가장 최근 수집된 키워드의 collected_at (없으면 null) */
    @Query("SELECT MAX(t.collectedAt) FROM TrendKeyword t")
    LocalDateTime findMaxCollectedAt();
    // 서버에서 ID를 수동처리하기 위한 코드
    // 중복 체크용 (데이터가 이미 있는지 확인)
    boolean existsByCollectedAtAndKeyword(LocalDateTime collectedAt, String keyword);

    TrendKeyword findFirstByOrderByIdDesc();

    List<TrendKeyword> findAllByCollectedAtGreaterThanEqualOrderByCollectedAtDesc(LocalDateTime start);

    List<TrendKeyword> findAllByCollectedAtGreaterThanEqualAndCollectedAtLessThanOrderByCollectedAtDesc(
        LocalDateTime start,
        LocalDateTime end
    );

    @Query("""
        SELECT t
        FROM TrendKeyword t
        WHERE t.collectedAt >= :start
          AND t.collectedAt < :end
          AND NOT EXISTS (
              SELECT p.id
              FROM Product p
              WHERE p.keyword = t
          )
        ORDER BY t.collectedAt DESC
        """)
    List<TrendKeyword> findRpaDeletableKeywords(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /** RPA 후속 파싱에 사용할 당일 키워드 조회 */
    @Query("""
        SELECT t
        FROM TrendKeyword t
        WHERE t.collectedAt >= :start
          AND t.id > 0
          AND t.rankingId IS NOT NULL
          AND t.syncDate IS NOT NULL
        ORDER BY t.id DESC
        """)
    List<TrendKeyword> findRecentRpaReadyKeywords(
        @Param("start") LocalDateTime start,
        Pageable pageable
    );
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
