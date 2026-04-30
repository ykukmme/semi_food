ALTER TABLE product
    MODIFY COLUMN product_url VARCHAR(1000) COMMENT '상품 구매처 링크';

SET @product_stock_column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'product'
      AND COLUMN_NAME = 'stock'
);
SET @product_stock_ddl = IF(
    @product_stock_column_exists = 0,
    'ALTER TABLE product ADD COLUMN stock INT NOT NULL DEFAULT 0 COMMENT ''재고 수량''',
    'SELECT 1'
);
PREPARE product_stock_stmt FROM @product_stock_ddl;
EXECUTE product_stock_stmt;
DEALLOCATE PREPARE product_stock_stmt;

SET @product_available_stock_column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'product'
      AND COLUMN_NAME = 'available_stock'
);
SET @product_available_stock_ddl = IF(
    @product_available_stock_column_exists = 0,
    'ALTER TABLE product ADD COLUMN available_stock INT NOT NULL DEFAULT 0 COMMENT ''주문 가능 수량''',
    'SELECT 1'
);
PREPARE product_available_stock_stmt FROM @product_available_stock_ddl;
EXECUTE product_available_stock_stmt;
DEALLOCATE PREPARE product_available_stock_stmt;

SET @product_reg_date_column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'product'
      AND COLUMN_NAME = 'reg_date'
);
SET @product_reg_date_ddl = IF(
    @product_reg_date_column_exists = 0,
    'ALTER TABLE product ADD COLUMN reg_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''등록 일시''',
    'SELECT 1'
);
PREPARE product_reg_date_stmt FROM @product_reg_date_ddl;
EXECUTE product_reg_date_stmt;
DEALLOCATE PREPARE product_reg_date_stmt;

SET @product_mod_date_column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'product'
      AND COLUMN_NAME = 'mod_date'
);
SET @product_mod_date_ddl = IF(
    @product_mod_date_column_exists = 0,
    'ALTER TABLE product ADD COLUMN mod_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''수정 일시''',
    'SELECT 1'
);
PREPARE product_mod_date_stmt FROM @product_mod_date_ddl;
EXECUTE product_mod_date_stmt;
DEALLOCATE PREPARE product_mod_date_stmt;

SET @product_del_date_column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'product'
      AND COLUMN_NAME = 'del_date'
);
SET @product_del_date_ddl = IF(
    @product_del_date_column_exists = 0,
    'ALTER TABLE product ADD COLUMN del_date DATETIME COMMENT ''삭제 일시''',
    'SELECT 1'
);
PREPARE product_del_date_stmt FROM @product_del_date_ddl;
EXECUTE product_del_date_stmt;
DEALLOCATE PREPARE product_del_date_stmt;

SET @order_subtotal_column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'purchase_order'
      AND COLUMN_NAME = 'subtotal'
);
SET @order_subtotal_ddl = IF(
    @order_subtotal_column_exists = 0,
    'ALTER TABLE purchase_order ADD COLUMN subtotal INT NOT NULL DEFAULT 0 COMMENT ''소계 (원)'' AFTER is_auto',
    'SELECT 1'
);
PREPARE order_subtotal_stmt FROM @order_subtotal_ddl;
EXECUTE order_subtotal_stmt;
DEALLOCATE PREPARE order_subtotal_stmt;

SET @order_shipping_address_column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'purchase_order'
      AND COLUMN_NAME = 'shipping_address'
);
SET @order_shipping_address_ddl = IF(
    @order_shipping_address_column_exists = 0,
    'ALTER TABLE purchase_order ADD COLUMN shipping_address VARCHAR(500) COMMENT ''배송지 주소'' AFTER subtotal',
    'SELECT 1'
);
PREPARE order_shipping_address_stmt FROM @order_shipping_address_ddl;
EXECUTE order_shipping_address_stmt;
DEALLOCATE PREPARE order_shipping_address_stmt;

SET @order_payment_method_column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'purchase_order'
      AND COLUMN_NAME = 'payment_method'
);
SET @order_payment_method_ddl = IF(
    @order_payment_method_column_exists = 0,
    'ALTER TABLE purchase_order ADD COLUMN payment_method VARCHAR(50) COMMENT ''결제수단'' AFTER shipping_address',
    'SELECT 1'
);
PREPARE order_payment_method_stmt FROM @order_payment_method_ddl;
EXECUTE order_payment_method_stmt;
DEALLOCATE PREPARE order_payment_method_stmt;

SET @order_payment_status_column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'purchase_order'
      AND COLUMN_NAME = 'payment_status'
);
SET @order_payment_status_ddl = IF(
    @order_payment_status_column_exists = 0,
    'ALTER TABLE purchase_order ADD COLUMN payment_status VARCHAR(20) COMMENT ''결제상태'' AFTER payment_method',
    'SELECT 1'
);
PREPARE order_payment_status_stmt FROM @order_payment_status_ddl;
EXECUTE order_payment_status_stmt;
DEALLOCATE PREPARE order_payment_status_stmt;
