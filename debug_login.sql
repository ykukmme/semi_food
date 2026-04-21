-- 로그인 문제 디버깅용 SQL
-- admin 계정의 현재 상태 확인

SELECT 
    id,
    member_id,
    password,
    email,
    phone,
    name,
    role,
    created_at,
    -- BCrypt 해시 비교를 위한 정보
    LENGTH(password) as password_length,
    SUBSTRING(password, 1, 7) as password_prefix
FROM member 
WHERE member_id = 'admin';

-- password123의 올바른 BCrypt 해시와 비교
SELECT 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' as correct_hash,
    password as current_hash,
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi' = password as is_correct
FROM member 
WHERE member_id = 'admin';
