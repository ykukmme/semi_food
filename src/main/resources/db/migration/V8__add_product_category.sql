ALTER TABLE product
    ADD COLUMN category VARCHAR(30) NOT NULL DEFAULT 'AGRICULTURAL' COMMENT '상품 카테고리';

UPDATE product
SET category = CASE
    WHEN name LIKE '%선물%' OR name LIKE '%세트%' OR name LIKE '%기프트%' OR name LIKE '%박스%' OR name LIKE '%명절%'
        THEN 'GIFT'
    WHEN name LIKE '%즙%' OR name LIKE '%잼%' OR name LIKE '%청%' OR name LIKE '%장아찌%' OR name LIKE '%장%'
        OR name LIKE '%소스%' OR name LIKE '%건조%' OR name LIKE '%말린%' OR name LIKE '%분말%' OR name LIKE '%가공%'
        THEN 'PROCESSED'
    WHEN name LIKE '%전복%' OR name LIKE '%멸치%' OR name LIKE '%김%' OR name LIKE '%미역%' OR name LIKE '%다시마%'
        OR name LIKE '%굴%' OR name LIKE '%새우%' OR name LIKE '%문어%' OR name LIKE '%오징어%' OR name LIKE '%생선%'
        OR name LIKE '%갈치%' OR name LIKE '%고등어%' OR name LIKE '%수산%' OR name LIKE '%낙지%'
        THEN 'MARINE'
    ELSE 'AGRICULTURAL'
END;
