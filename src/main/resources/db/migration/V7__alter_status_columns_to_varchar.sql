-- purchase_order.status: ENUM → VARCHAR(20)
-- 이유: ENUM 컬럼에 새 값 추가 시 ALTER TABLE 재구성 비용 발생.
--       VARCHAR + 애플리케이션 Enum 조합으로 유연성 확보.
ALTER TABLE purchase_order
    MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'RECEIVED'
        COMMENT '발주 상태 (RECEIVED/IN_PROGRESS/SHIPPED/COMPLETED/CANCELLED)';

-- rpa_log.status: ENUM → VARCHAR(10)
ALTER TABLE rpa_log
    MODIFY COLUMN status VARCHAR(10) NOT NULL
        COMMENT 'RPA 상태 (RUNNING/COMPLETED/FAILED)';
