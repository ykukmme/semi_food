package com.semi.domain.rpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RPA 실행 로그 엔티티
 * 대시보드의 'RPA 가동 여부' 판별: 최신 레코드의 status 확인
 */
@Entity
@Table(name = "rpa_log")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RpaLog {

    @Id
    // [ ]TODO TiDB에서는 AUTO_INCREMENT 지원이 제한적이므로 주석처리, DB 변경시 다시 활성화 필요
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RpaStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;  // RUNNING 중에는 null

    @Column(name = "keyword_count")
    private Integer keywordCount;  // 수집된 키워드 수

    @Column(name = "product_count")
    private Integer productCount;  // 수집된 상품 수

    @Column(columnDefinition = "TEXT")
    private String message;  // 오류 메시지 또는 요약

    @Builder
    public RpaLog(LocalDateTime startedAt) {
        this.status    = RpaStatus.RUNNING;  // 시작 시 RUNNING
        this.startedAt = startedAt;
    }

    /** RPA 정상 완료 처리 */
    public void complete(Integer keywordCount, Integer productCount) {
        this.status       = RpaStatus.COMPLETED;
        this.endedAt      = LocalDateTime.now();
        this.keywordCount = keywordCount;
        this.productCount = productCount;
    }

    /** RPA 오류 종료 처리 */
    public void fail(String message) {
        this.status  = RpaStatus.FAILED;
        this.endedAt = LocalDateTime.now();
        this.message = message;
    }
}
