package com.semi.domain.keyword;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;


//[x]TODO <syncDate>20260426</syncDate> api 파싱을 통한 데이터의 경우 전날을 기준으로 syncDate가 적혀오는 거 같아서, DB에서 처리 할 때 기준 통일이 필요해 보임.
//[ ]TODO api 에서 syncDate와 rankingID 를 조합하면 일정한 제품 리스트가 나옴. 별도의 칼럼을 추가해서 관리 할 것, 파이썬 코드도 업데이트가 필요함.
        // 확인결과 간헐적으로 불발
// [ ] TODO DB에 데이터를 넣은 다음 작업을 시작하려 했으나 농속성으로 불발.

/**
 * 트렌드 키워드 엔티티
 * RPA가 네이버 쇼핑 식품 카테고리에서 수집한 키워드 (1~20위)
 */
@Entity
@Table(name = "trend_keyword")
@Data  // @Data, @Builder, @AllArgsConstructor, @NoArgsConstructor(access = AccessLevel.PROTECTED) 이 위치에 없을시 rpa/parser 패키지 쪽에서 에러 발생
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendKeyword {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // TiDB의 ID생성 문제로 주석처리
    private Long id; 

    @Column(nullable = false, length = 100)
    private String keyword;

    @Column(name = "`rank`")  // rank는 MySQL 8.0 예약어 — 백틱 필수
    private Integer rank;  // 1~10위 (Byte는 null 언박싱 시 NPE 위험)

    @Column(nullable = false)
    private Integer frequency;  // 키워드 빈도수

    @Column(name = "collected_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime collectedAt;  // 수집 시간

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;  // 현재 활성 여부
    
    // 2026_04_28_Tue_1536 파싱에서 얻은 데이터 처리용
    @Column(name = "ranking_id", nullable = true)
    private Long rankingId;

    @Column(name = "sync_date", nullable = true) // url에서 syncDate, ymd 등으로 쓰임, 기존크롤링에서는 수집불가했던 데이터
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime syncDate;


    // public TrendKeyword(String keyword, Integer rank, Integer frequency, LocalDateTime collectedAt) {
    //     this.keyword     = keyword;
    //     this.rank        = rank;
    //     this.frequency   = frequency;
    //     this.collectedAt = collectedAt;
    //     this.isActive    = true;  // 기본값: 활성
    // }

    // public TrendKeyword(Long id, String keyword, Integer rank, Integer frequency, LocalDateTime collectedAt) {
    //     this.id          = id;
    //     this.keyword     = keyword;
    //     this.rank        = rank;
    //     this.frequency   = frequency;
    //     this.collectedAt = collectedAt;
    //     this.isActive    = true;  // 기본값: 활성
    // }

    /** RPA 재수집 시 기존 키워드 비활성화 */
    public void deactivate() {
        this.isActive = false;
    }

    /** 빈도수 갱신 */
    public void updateFrequency(Integer frequency) {
        this.frequency = frequency;
    }
}
