

-- [ ] TODO 시간관계상 보류
-- 완료 케이스
INSERT INTO food.rpa_log (status, started_at, ended_at, keyword_count, product_count, message) VALUES
('COMPLETED', '2024-04-20 09:00:00', '2024-04-20 09:15:30', 50, 1200, '정상적으로 수집 완료되었습니다.'),
('COMPLETED', '2024-04-21 10:00:00', '2024-04-21 10:05:12', 10, 250, '데이터 동기화 완료.');

-- 진행 중 케이스
INSERT INTO food.rpa_log (status, started_at, ended_at, keyword_count, product_count, message) VALUES
('RUNNING', '2024-04-24 09:30:00', NULL, 15, 450, '키워드 분석 단계 진행 중...'),
('RUNNING', '2024-04-24 09:50:00', NULL, 5, 120, '상품 목록 로드 중');

-- 실패 케이스
INSERT INTO food.rpa_log (status, started_at, ended_at, keyword_count, product_count, message) VALUES
('FAILED', '2024-04-22 14:00:00', '2024-04-22 14:02:15', 30, 0, '네트워크 타임아웃 발생'),
('FAILED', '2024-04-23 11:00:00', '2024-04-23 11:01:05', 0, 0, '로그인 정보 유효하지 않음');
