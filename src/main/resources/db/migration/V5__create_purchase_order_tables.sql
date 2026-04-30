-- 발주 테이블
-- 사용자가 장바구니에서 발주한 내역 (1건 = 1구매처)
CREATE TABLE purchase_order (
    id           BIGINT      AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(20) NOT NULL UNIQUE COMMENT '발주번호 (PO-YYYYMMDD-NNNN)',
    member_id    BIGINT      NOT NULL COMMENT '발주자',
    supplier_id  BIGINT      NOT NULL COMMENT '구매처',
    status       ENUM('RECEIVED', 'IN_PROGRESS', 'SHIPPED', 'COMPLETED')
                             NOT NULL DEFAULT 'RECEIVED' COMMENT '발주 상태',
    total_price  INT         NOT NULL COMMENT '총 가격 (원)',
    shipping_fee INT         NOT NULL DEFAULT 0 COMMENT '배송비 (원)',
    is_auto      BOOLEAN     NOT NULL DEFAULT FALSE COMMENT '자동발주 여부',
    ordered_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '발주일시',

    CONSTRAINT fk_order_member   FOREIGN KEY (member_id)   REFERENCES member (id),
    CONSTRAINT fk_order_supplier FOREIGN KEY (supplier_id) REFERENCES supplier (id)
);

-- 발주 상품 명세 테이블
-- 발주서 PDF 항목의 원본 데이터 (상품명·가격은 발주 시점 스냅샷)
CREATE TABLE purchase_order_item (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT       NOT NULL COMMENT '발주',
    product_id   BIGINT       NOT NULL COMMENT '상품',
    product_name VARCHAR(200) NOT NULL COMMENT '상품명 스냅샷 (발주 시점)',
    price        INT          NOT NULL COMMENT '단가 스냅샷 (발주 시점)',
    quantity     INT          NOT NULL COMMENT '수량',

    CONSTRAINT fk_order_item_order   FOREIGN KEY (order_id)   REFERENCES purchase_order (id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product (id)
);
