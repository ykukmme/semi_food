package com.semi.domain.keyword;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 트렌드 키워드 엔티티
 * RPA가 네이버 쇼핑 식품 카테고리에서 수집한 키워드 (1~10위)
 */
@Entity
@Table(name = "trend_keyword")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Column(name = "`rank`")  // rank는 MySQL 8.0 예약어 — 백틱 필수
    private Integer rank;  // 1~10위 (Byte는 null 언박싱 시 NPE 위험)

    @Column(nullable = false)
    private Integer frequency;  // 키워드 빈도수

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;  // 수집 시간

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;  // 현재 활성 여부

    @Builder
    public TrendKeyword(String keyword, Integer rank, Integer frequency, LocalDateTime collectedAt) {
        this.keyword     = keyword;
        this.rank        = rank;
        this.frequency   = frequency;
        this.collectedAt = collectedAt;
        this.isActive    = true;  // 기본값: 활성
    }

    /** RPA 재수집 시 기존 키워드 비활성화 */
    public void deactivate() {
        this.isActive = false;
    }

    /** 빈도수 갱신 */
    public void updateFrequency(Integer frequency) {
        this.frequency = frequency;
    }
}
