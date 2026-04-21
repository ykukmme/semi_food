-- admin 계정 비밀번호를 'admin123'으로 변경
-- BCrypt 해시 생성: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi

UPDATE member 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' 
WHERE member_id = 'admin';

-- 변경 확인
SELECT member_id, password, email, phone, name, role, created_at 
FROM member 
WHERE member_id = 'admin';
