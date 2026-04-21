-- Flyway 마이그레이션 강제 실행 스크립트
-- V9 마이그레이션을 다시 실행하여 올바른 비밀번호 해시 적용

-- V9 마이그레이션을 이미 실행된 것으로 표시
INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, execution_time, success)
VALUES (1, '9', 'insert sample members', 'SQL', 'V9__insert_sample_members.sql', '0', 'admin', 1000, true);

-- V9 마이그레이션의 admin 비밀번호 해시 업데이트
UPDATE member SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' WHERE member_id = 'admin';
