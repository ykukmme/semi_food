-- Insert sample members for testing admin functionality
-- Password for all users: password123 (BCrypt hash)

-- Admin users
INSERT IGNORE INTO member (member_id, password, email, phone, name, role, created_at) 
VALUES 
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin@dadream.com', '010-1234-5678', 'Admin User', 'ADMIN', NOW()),
('manager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'manager@dadream.com', '010-2345-6789', 'Manager User', 'ADMIN', NOW()),
('admin2', '$2a$10$1NFvC8q7icF.jN7HjlytfuyFgighRZPB8fBwYnBE/.YL2lj7Zpele', 'admin2@dadream.com', '010-1234-5678', 'Admin User', 'ADMIN', NOW());

-- Regular users (password: 22222222)
INSERT IGNORE INTO member (member_id, password, email, phone, name, role, created_at) 
VALUES 
('honggildong', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'hong@dadream.com', '010-3456-7890', 'Hong Gildong', 'USER', NOW()),
('kimiron', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'kim@dadream.com', '010-4567-8901', 'Kim Iron', 'USER', NOW()),
('leejaeyong', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'lee@dadream.com', '010-5678-9012', 'Lee Jaeyong', 'USER', NOW()),
('parkminjun', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'park@dadream.com', '010-6789-0123', 'Park Minjun', 'USER', NOW()),
('choiyuna', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'choi@dadream.com', '010-7890-1234', 'Choi Yuna', 'USER', NOW()),
('jungdahye', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'jung@dadream.com', '010-8901-2345', 'Jung Dahye', 'USER', NOW()),
('kimsujin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'kimsujin@dadream.com', '010-9012-3456', 'Kim Sujin', 'USER', NOW()),
('songjongkook', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'song@dadream.com', '010-0123-4567', 'Song Jongkook', 'USER', NOW()),
('hanhyojoo', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'han@dadream.com', '010-1234-5678', 'Han Hyojoo', 'USER', NOW());


