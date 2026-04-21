-- Insert sample products for testing product management functionality

-- First, ensure we have sample keywords and suppliers
INSERT IGNORE INTO trend_keyword (keyword, `rank`, frequency, collected_at, is_active) VALUES
('Organic', 1, 850, NOW(), true),
('Fresh', 2, 720, NOW(), true),
('Premium', 3, 680, NOW(), true),
('Seasonal', 4, 590, NOW(), true),
('Local Farm', 5, 450, NOW(), true);

INSERT IGNORE INTO supplier (name, url, created_at) VALUES
('Naver Smart Store', 'https://smartstore.naver.com', NOW()),
('Kurly', 'https://www.kurly.com', NOW()),
('Coupang', 'https://www.coupang.com', NOW()),
('Market Kurly', 'https://market.kurly.com', NOW());

-- Get keyword and supplier IDs
SET @organic_id = (SELECT id FROM trend_keyword WHERE keyword = 'Organic' LIMIT 1);
SET @fresh_id = (SELECT id FROM trend_keyword WHERE keyword = 'Fresh' LIMIT 1);
SET @premium_id = (SELECT id FROM trend_keyword WHERE keyword = 'Premium' LIMIT 1);
SET @naver_id = (SELECT id FROM supplier WHERE name = 'Naver Smart Store' LIMIT 1);
SET @kurly_id = (SELECT id FROM supplier WHERE name = 'Kurly' LIMIT 1);
SET @coupang_id = (SELECT id FROM supplier WHERE name = 'Coupang' LIMIT 1);

-- Insert sample products
INSERT INTO product (
    keyword_id, supplier_id, name, description, price, image_url, product_url, 
    auto_order, crawled_at
) VALUES 
-- Organic products
(@organic_id, @naver_id, 'Organic Cherry Tomatoes 500g', 'Fresh organic cherry tomatoes from local farm', 7900, 
 'https://example.com/tomato.jpg', 'https://naver.com/product/1', false, NOW()),
(@organic_id, @kurly_id, 'Organic Mixed Salad Pack', 'Ready-to-eat mixed organic greens', 9800, 
 'https://example.com/salad.jpg', 'https://kurly.com/product/1', false, NOW()),
(@organic_id, @coupang_id, 'Organic Kale 200g', 'Fresh organic kale perfect for salads', 4500, 
 'https://example.com/kale.jpg', 'https://coupang.com/product/1', false, NOW()),

-- Fresh products  
(@fresh_id, @naver_id, 'Fresh Strawberries 500g', 'Sweet and juicy fresh strawberries', 18000, 
 'https://example.com/strawberry.jpg', 'https://naver.com/product/2', false, NOW()),
(@fresh_id, @kurly_id, 'Fresh Blueberries 200g', 'Premium fresh blueberries', 15000, 
 'https://example.com/blueberry.jpg', 'https://kurly.com/product/2', false, NOW()),
(@fresh_id, @coupang_id, 'Fresh Raspberries 125g', 'Delicate fresh raspberries', 12000, 
 'https://example.com/raspberry.jpg', 'https://coupang.com/product/2', false, NOW()),

-- Premium products
(@premium_id, @naver_id, 'Premium Avocado 2pcs', 'Ripe Hass avocados ready to eat', 12800, 
 'https://example.com/avocado.jpg', 'https://naver.com/product/3', false, NOW()),
(@premium_id, @kurly_id, 'Premium Mango 1kg', 'Sweet premium mangoes', 25000, 
 'https://example.com/mango.jpg', 'https://kurly.com/product/3', false, NOW()),
(@premium_id, @coupang_id, 'Premium Pineapple 1pc', 'Sweet golden pineapple', 8900, 
 'https://example.com/pineapple.jpg', 'https://coupang.com/product/3', false, NOW()),

-- Seasonal products
(@fresh_id, @naver_id, 'Seasonal Apple 5kg', 'Crisp seasonal apples', 35000, 
 'https://example.com/apple.jpg', 'https://naver.com/product/4', false, NOW()),
(@organic_id, @kurly_id, 'Seasonal Peach 2kg', 'Sweet seasonal peaches', 28000, 
 'https://example.com/peach.jpg', 'https://kurly.com/product/4', false, NOW()),
(@premium_id, @coupang_id, 'Seasonal Grapes 1kg', 'Juicy seasonal grapes', 22000, 
 'https://example.com/grape.jpg', 'https://coupang.com/product/4', false, NOW());

-- Update some products to have low stock for testing
-- Note: Since we don't have a stock column in the product table, we'll use the price to simulate stock levels
-- In a real implementation, you would have a separate inventory table
