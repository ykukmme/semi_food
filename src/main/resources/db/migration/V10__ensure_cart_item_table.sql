CREATE TABLE IF NOT EXISTS cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL COMMENT 'Member ID',
    product_id BIGINT NOT NULL COMMENT 'Product ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT 'Quantity',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created at',

    CONSTRAINT fk_cart_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT uq_cart_member_product UNIQUE (member_id, product_id)
);
