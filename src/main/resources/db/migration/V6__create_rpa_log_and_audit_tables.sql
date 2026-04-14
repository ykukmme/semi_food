-- RPA 실행 로그 테이블
-- 대시보드의 'RPA 가동 여부' 판별 기준: 최신 레코드의 status
CREATE TABLE rpa_log (
    id            BIGINT   AUTO_INCREMENT PRIMARY KEY,
    status        ENUM('RUNNING', 'COMPLETED', 'FAILED') NOT NULL COMMENT 'RPA 상태',
    started_at    DATETIME NOT NULL COMMENT '시작 시간',
    ended_at      DATETIME COMMENT '종료 시간 (RUNNING 중에는 NULL)',
    keyword_count INT      COMMENT '수집된 키워드 수',
    product_count INT      COMMENT '수집된 상품 수',
    message       TEXT     COMMENT '오류 메시지 또는 요약'
);

-- 자동발주 플래그 변경 이력 테이블
-- Hard Rule: auto_order 변경 시 반드시 이 테이블에 기록
CREATE TABLE auto_order_audit (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT       NOT NULL COMMENT '대상 상품',
    changed_by BIGINT       NOT NULL COMMENT '변경한 관리자',
    old_value  BOOLEAN      NOT NULL COMMENT '변경 전 값',
    new_value  BOOLEAN      NOT NULL COMMENT '변경 후 값',
    changed_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '변경 일시',
    reason     VARCHAR(200) COMMENT '변경 사유',

    CONSTRAINT fk_audit_product FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT fk_audit_member  FOREIGN KEY (changed_by) REFERENCES member (id)
);
