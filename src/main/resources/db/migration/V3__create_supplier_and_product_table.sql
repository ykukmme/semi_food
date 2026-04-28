-- 공급업체 테이블
-- RPA 크롤링 시 판매자 정보로 자동 생성
CREATE TABLE supplier (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE COMMENT '구매처명 (발주서에 기재)',
    url        VARCHAR(500) COMMENT '구매처 홈 링크',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시'
);

-- 상품 테이블
-- RPA가 크롤링한 상품 정보 (키워드당 2개)
CREATE TABLE IF NOT EXISTS product (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    keyword_id  BIGINT       NOT NULL COMMENT '수집 출처 키워드',
    supplier_id BIGINT       NOT NULL COMMENT '공급업체',
    name        VARCHAR(200) NOT NULL COMMENT '상품명',
    description TEXT         COMMENT '상품 설명',
    price       INT          NOT NULL COMMENT '가격 (원)',
    image_url   VARCHAR(500) COMMENT '이미지 주소',
    product_url VARCHAR(1000) COMMENT '상품 구매처 링크',
    auto_order  BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '자동발주 플래그 (기본 OFF)',
    stock       INT          NOT NULL DEFAULT 0 COMMENT '재고 수량',
    available_stock INT      NOT NULL DEFAULT 0 COMMENT '주문 가능 수량',
    crawled_at  DATETIME     NOT NULL COMMENT '크롤링 시간',
    reg_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록 일시',
    mod_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    del_date DATETIME COMMENT '삭제 일시',

    CONSTRAINT fk_product_keyword  FOREIGN KEY (keyword_id)  REFERENCES trend_keyword (id),
    CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id) REFERENCES supplier (id)
);
