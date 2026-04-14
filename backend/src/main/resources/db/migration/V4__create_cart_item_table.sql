-- 장바구니 테이블
-- 사용자가 발주 전 담아두는 상품 목록
CREATE TABLE cart_item (
    id         BIGINT   AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT   NOT NULL COMMENT '회원',
    product_id BIGINT   NOT NULL COMMENT '상품',
    quantity   INT      NOT NULL DEFAULT 1 COMMENT '수량',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '담은 일시',

    CONSTRAINT fk_cart_member  FOREIGN KEY (member_id)  REFERENCES member (id),
    CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT uq_cart_member_product UNIQUE (member_id, product_id)  -- 동일 상품 중복 방지
);
