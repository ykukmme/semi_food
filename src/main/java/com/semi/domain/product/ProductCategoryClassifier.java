package com.semi.domain.product;

import java.util.Locale;

public final class ProductCategoryClassifier {

    private ProductCategoryClassifier() {
    }

    public static ProductCategory classify(String productName) {
        String name = normalize(productName);

        // 1. GIFT — 명확한 선물 포장 표현만 인정
        //   "선물용" 제거: "사과 가정용 선물용" 같은 식재료 본질 상품을 가로채던 문제
        //   세트/박스 형태로 포장된 명시 GIFT 신호만 유지
        if (containsAny(name, "선물세트", "선물박스", "기프트", "명절", "답례품")) {
            return ProductCategory.GIFT;
        }

        // 2. MEAT — 육류 부위명 위주 (단일 글자 "닭"/"돼지"/"소"는 광범위 매칭으로 제외)
        //   가공 형태(냉동/훈제 닭가슴살)도 신선육 본질 유지
        if (containsAny(name, "닭가슴살", "닭다리", "닭안심", "닭정육",
                        "삼겹살", "오겹살", "목살", "항정살", "갈매기살", "가브리살",
                        "안심", "등심", "갈비", "차돌", "사태", "양지", "우삼겹", "육포",
                        "한우", "한돈", "흑돼지", "흑돈")) {
            return ProductCategory.MEAT;
        }

        // 2.5 PROCESSED (완성형) — 식재료 베이스가 수산/육류여도 인스턴트/완성식품은 가공품
        //   예: "멸치 쌀국수", "닭갈비 라면" → PROCESSED
        if (containsAny(name, "쌀국수", "라면", "우동", "냉면", "수프", "스프",
                        "카레", "짜장", "잡채", "떡볶이")) {
            return ProductCategory.PROCESSED;
        }

        // 3. MARINE — 어종/해산물 원물 우선 (가공 형태여도 수산 본질 유지: 건조 미역, 멸치 분말 등)
        if (containsAny(name, "전복", "멸치", "김", "미역", "다시마", "굴", "쭈꾸미", "새우", "문어",
                        "오징어", "생선", "갈치", "고등어", "꽃게", "조개", "수산")) {
            return ProductCategory.MARINE;
        }

        // 3.5 raw 재료 가드 — "장아찌용"/"절임용"은 raw 식재료 표현이므로 PROCESSED 매칭 전 단락
        if (containsAny(name, "장아찌용", "절임용")) {
            return ProductCategory.AGRICULTURAL;
        }

        // 3. PROCESSED — 명확한 가공 표현
        //   제거된 광범위 키워드: "장"(농장/장아찌/시장 매칭), "종"(종류/종합), "팩"(포장단위), "답례품"(GIFT 로 이동), "청"(청송/청도/청정원 등 지명·브랜드 매칭)
        //   "장"의 본 의도(간장/된장), "청"의 본 의도(매실청/유자청)는 명시 키워드로 분리
        if (containsAny(name,
                        // 일반 가공 표현
                        "즙", "잼", "장아찌", "간장", "된장", "고추장", "쌈장",
                        "매실청", "오미자청", "유자청", "도라지청", "생강청", "레몬청",
                        "소스", "건조", "말린", "분말", "스팀", "훈제", "냉동", "가공",
                        // 떡류 (가공품)
                        "버터떡", "백설기", "시루떡", "인절미", "가래떡", "꿀떡",
                        "쑥떡", "콩떡", "콩달떡", "개떡", "약밥", "찹쌀떡",
                        // 영양식·환자식·기능식
                        "영양식", "균형영양식", "영양조제식품", "영양음료", "환자식", "시니어식",
                        // 단백질/저당 가공식품
                        "단백질바", "프로틴바", "두부면", "두유면", "단백질음료",
                        // 유지·소스·감미료
                        "올리브오일", "마요네즈", "알룰로스", "mct오일")) {
            return ProductCategory.PROCESSED;
        }

        // 4. AGRICULTURAL — 농산물 명시 키워드 + 산나물류 보강
        if (containsAny(name, "시금치", "마늘", "쌀", "현미", "사과", "배", "감",
                        "고구마", "감자", "파프리카", "양파", "당근", "버섯",
                        "채소", "과일", "산지직송", "농산",
                        "두릅", "곰취", "봄나물", "산나물", "나물")) {
            return ProductCategory.AGRICULTURAL;
        }

        // catch-all — 모르면 가공품 (RPA 수집 데이터 특성상 미매칭의 대부분이 브랜드 가공식품)
        //   기존엔 AGRICULTURAL 폴백이라 농산물 탭이 비대화되고 마이노멀/뉴케어/떡류가 휩쓸려 들어감
        return ProductCategory.PROCESSED;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
    }

    private static boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
