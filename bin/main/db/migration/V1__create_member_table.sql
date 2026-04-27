-- 회원 테이블
CREATE TABLE IF NOT EXISTS member (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   VARCHAR(50)  NOT NULL UNIQUE COMMENT '로그인용 아이디',
    password    VARCHAR(255) NOT NULL COMMENT 'BCrypt 해시',
    email       VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일',
    phone       VARCHAR(20)  COMMENT '전화번호',
    name        VARCHAR(50)  NOT NULL COMMENT '이름',
    `role`      ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER' COMMENT '권한',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시'
);
