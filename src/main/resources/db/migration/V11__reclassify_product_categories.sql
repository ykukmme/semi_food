-- ProductCategoryClassifier (Java) 룰을 1회만 SQL로 미러링해 기존 row 일괄 보정.
-- V8/V9가 DEFAULT 'AGRICULTURAL'을 박고 RPA 적재기가 .category(...)를 호출하지 않아
-- 기존 모든 row가 AGRICULTURAL 로 강제 박힌 상태를 정상화.
-- 신규 row 는 RPA 빌더의 .category(ProductCategoryClassifier.classify(name)) 로 자동 처리됨.
-- 분류기 룰이 추후 변경되면 본 SQL은 갱신되지 않음 — 필요 시 신규 마이그레이션 추가.

UPDATE product
SET category = CASE
    -- 1. GIFT — 선물 포장 명시 (생활어 "선물용" 단독은 제외)
    WHEN LOWER(REPLACE(name, ' ', '')) REGEXP '선물세트|선물박스|기프트|명절|답례품' THEN 'GIFT'

    -- 2. MEAT — 부위명·고급육 위주
    WHEN LOWER(REPLACE(name, ' ', '')) REGEXP '닭가슴살|닭다리|닭안심|닭정육|삼겹살|오겹살|목살|항정살|갈매기살|가브리살|안심|등심|갈비|차돌|사태|양지|우삼겹|육포|한우|한돈|흑돼지|흑돈' THEN 'MEAT'

    -- 3. PROCESSED (완성형) — 식재료 베이스가 수산/육류여도 인스턴트면 가공
    WHEN LOWER(REPLACE(name, ' ', '')) REGEXP '쌀국수|라면|우동|냉면|수프|스프|카레|짜장|잡채|떡볶이' THEN 'PROCESSED'

    -- 4. MARINE — 어종/해산물 원물 우선
    WHEN LOWER(REPLACE(name, ' ', '')) REGEXP '전복|멸치|김|미역|다시마|굴|쭈꾸미|새우|문어|오징어|생선|갈치|고등어|꽃게|조개|수산' THEN 'MARINE'

    -- 5. raw 재료 가드 — 장아찌용/절임용은 raw 식재료
    WHEN LOWER(REPLACE(name, ' ', '')) REGEXP '장아찌용|절임용' THEN 'AGRICULTURAL'

    -- 6. PROCESSED — 명확한 가공 표현
    WHEN LOWER(REPLACE(name, ' ', '')) REGEXP '즙|잼|장아찌|간장|된장|고추장|쌈장|매실청|오미자청|유자청|도라지청|생강청|레몬청|소스|건조|말린|분말|스팀|훈제|냉동|가공|버터떡|백설기|시루떡|인절미|가래떡|꿀떡|쑥떡|콩떡|콩달떡|개떡|약밥|찹쌀떡|영양식|균형영양식|영양조제식품|영양음료|환자식|시니어식|단백질바|프로틴바|두부면|두유면|단백질음료|올리브오일|마요네즈|알룰로스|mct오일' THEN 'PROCESSED'

    -- 7. AGRICULTURAL — 농산물 명시 + 산나물류
    WHEN LOWER(REPLACE(name, ' ', '')) REGEXP '시금치|마늘|쌀|현미|사과|배|감|고구마|감자|파프리카|양파|당근|버섯|채소|과일|산지직송|농산|두릅|곰취|봄나물|산나물|나물' THEN 'AGRICULTURAL'

    -- 8. catch-all — 모르면 가공품 (Java 분류기와 동일)
    ELSE 'PROCESSED'
END
-- DEFAULT 'AGRICULTURAL'로 강제 박힌 row만 보정 (의도 명시 + 풀스캔 회피)
WHERE category = 'AGRICULTURAL';
