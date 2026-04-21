-- 데이터베이스 초기화 스크립트
-- 모든 테이블 삭제 후 Flyway가 다시 생성하도록 함

DROP TABLE IF EXISTS purchase_order_item;
DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS trend_keyword;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS flyway_schema_history;

-- 모든 데이터 삭제 완료
