-- 트렌드 키워드 테이블
-- RPA가 네이버 쇼핑 식품 카테고리에서 수집한 트렌드 키워드
CREATE TABLE trend_keyword (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    keyword      VARCHAR(100) NOT NULL COMMENT '키워드명',
    rank         TINYINT      COMMENT '순위 (1~10위)',
    frequency    INT          NOT NULL DEFAULT 0 COMMENT '키워드 빈도수 (대시보드 표시용)',
    collected_at DATETIME     NOT NULL COMMENT '수집 시간',
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE COMMENT '현재 활성 여부'
);
