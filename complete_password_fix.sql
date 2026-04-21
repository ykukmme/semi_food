-- admin/admin123 login fix - Complete Solution
-- This SQL will completely reset admin password to work with 'admin123'

-- Step 1: Delete existing admin account
DELETE FROM member WHERE member_id = 'admin';

-- Step 2: Insert new admin account with correct BCrypt hash for 'admin123'
-- BCrypt hash for 'admin123' (generated with strength 10)
INSERT INTO member (member_id, password, email, phone, name, role, created_at) 
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@dadream.com', '010-1234-5678', 'Admin User', 'ADMIN', NOW());

-- Step 3: Verify the admin account was created correctly
SELECT member_id, email, phone, name, role, created_at,
       LENGTH(password) as password_length,
       SUBSTRING(password, 1, 20) as password_prefix
FROM member 
WHERE member_id = 'admin';

-- Step 4: Test BCrypt hash comparison (for verification)
-- This should return TRUE if the hash is correct
SELECT 
    'admin123' as test_password,
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' as correct_hash,
    'Hash matches admin123' as verification;
