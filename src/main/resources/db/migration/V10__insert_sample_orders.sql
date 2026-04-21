-- Insert sample orders for testing order management functionality

-- First, insert sample suppliers
INSERT IGNORE INTO supplier (name, url, created_at) VALUES 
('Local Farm', 'https://local-farm.com', NOW()),
('Organic Wholesale', 'https://organic-wholesale.com', NOW()),
('Premium Distributor', 'https://premium-distributor.com', NOW());

-- Then, insert sample trend keywords
INSERT IGNORE INTO trend_keyword (keyword, `rank`, frequency, collected_at, is_active) VALUES 
('유기농산물', 1, 1500, NOW(), true),
('친환경제품', 2, 1200, NOW(), true),
('건강식품', 3, 980, NOW(), true),
('비건식품', 4, 750, NOW(), true),
('다이어트식단', 5, 620, NOW(), true);

-- Then, insert some sample products if they don't exist
INSERT IGNORE INTO product (name, description, price, auto_order, crawled_at, keyword_id, supplier_id) VALUES 
('Organic Cherry Tomatoes 500g', 'Fresh organic cherry tomatoes from local farm', 7900, false, NOW(), 1, 1),
('Fresh Kale 200g', 'Organic kale perfect for salads and juices', 4500, false, NOW(), 1, 1),
('Premium Avocado 2pcs', 'Ripe Hass avocados ready to eat', 12800, false, NOW(), 2, 2),
('Fresh Strawberries 500g', 'Sweet and juicy organic strawberries', 18000, false, NOW(), 2, 2),
('Mixed Salad Pack', 'Ready-to-eat mixed organic greens', 9800, false, NOW(), 3, 3);

-- Get member IDs for sample orders
SET @admin_id = (SELECT id FROM member WHERE member_id = 'admin' LIMIT 1);
SET @hong_id = (SELECT id FROM member WHERE member_id = 'honggildong' LIMIT 1);
SET @kim_id = (SELECT id FROM member WHERE member_id = 'kimiron' LIMIT 1);
SET @lee_id = (SELECT id FROM member WHERE member_id = 'leejaeyong' LIMIT 1);
SET @park_id = (SELECT id FROM member WHERE member_id = 'parkminjun' LIMIT 1);

-- Insert sample purchase orders
INSERT INTO purchase_order (
    order_number, member_id, supplier_id, status, total_price, shipping_fee, 
    ordered_at
) VALUES 
('ORD-2026-8842', @hong_id, 1, 'PROCESSING', 45600, 0, NOW()),
('ORD-2026-8841', @kim_id, 2, 'QUEUED', 12800, 0, NOW()),
('ORD-2026-8840', @lee_id, 3, 'FAILED', 89000, 0, NOW()),
('ORD-2026-8839', @park_id, 1, 'PROCESSING', 34500, 0, NOW()),
('ORD-2026-8838', @hong_id, 2, 'PROCESSING', 18000, 0, NOW());

-- Insert purchase order items
INSERT INTO purchase_order_item (
    order_id, product_id, product_name, quantity, price
) VALUES 
-- Order 8842 items
((SELECT id FROM purchase_order WHERE order_number = 'ORD-2026-8842'), 
 (SELECT id FROM product WHERE name = 'Organic Cherry Tomatoes 500g'), 
 'Organic Cherry Tomatoes 500g', 1, 18500),
((SELECT id FROM purchase_order WHERE order_number = 'ORD-2026-8842'), 
 (SELECT id FROM product WHERE name = 'Fresh Kale 200g'), 
 'Fresh Kale 200g', 2, 11050),
((SELECT id FROM purchase_order WHERE order_number = 'ORD-2026-8842'), 
 (SELECT id FROM product WHERE name = 'Mixed Salad Pack'), 
 'Mixed Salad Pack', 1, 5000),

-- Order 8841 items
((SELECT id FROM purchase_order WHERE order_number = 'ORD-2026-8841'), 
 (SELECT id FROM product WHERE name = 'Mixed Salad Pack'), 
 'Mixed Salad Pack', 1, 12800),

-- Order 8840 items
((SELECT id FROM purchase_order WHERE order_number = 'ORD-2026-8840'), 
 (SELECT id FROM product WHERE name = 'Premium Avocado 2pcs'), 
 'Premium Avocado 2pcs', 1, 89000),

-- Order 8839 items
((SELECT id FROM purchase_order WHERE order_number = 'ORD-2026-8839'), 
 (SELECT id FROM product WHERE name = 'Premium Avocado 2pcs'), 
 'Premium Avocado 2pcs', 2, 17250),

-- Order 8838 items
((SELECT id FROM purchase_order WHERE order_number = 'ORD-2026-8838'), 
 (SELECT id FROM product WHERE name = 'Fresh Strawberries 500g'), 
 'Fresh Strawberries 500g', 1, 18000);

