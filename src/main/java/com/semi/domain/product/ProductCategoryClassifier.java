package com.semi.domain.product;

import java.util.Locale;

public final class ProductCategoryClassifier {

    private ProductCategoryClassifier() {
    }

    public static ProductCategory classify(String productName) {
        String name = normalize(productName);

        // 1. GIFT — 선물/답례품 명시 키워드만 (단어 일부 매칭으로 인한 오분류 줄이기 위해 구체화)
        if (containsAny(name, "선물세트", "선물박스", "선물용", "기프트", "명절", "답례품")) {
            return ProductCategory.GIFT;
        }

        // 2. MARINE — 어종/해산물 원물 우선 (가공 형태여도 수산 본질 유지: 건조 미역, 멸치 분말 등)
        if (containsAny(name, "전복", "멸치", "김", "미역", "다시마", "굴", "쭈꾸미", "새우", "문어",
                        "오징어", "생선", "갈치", "고등어", "꽃게", "조개", "수산")) {
            return ProductCategory.MARINE;
        }

        // 3. PROCESSED — 명확한 가공 표현
        //   제거된 광범위 키워드: "장"(농장/장아찌/시장 매칭), "종"(종류/종합), "팩"(포장단위), "답례품"(GIFT 로 이동)
        //   "장"의 본 의도(간장/된장)는 명시 키워드로 분리
        if (containsAny(name, "즙", "잼", "청", "장아찌", "간장", "된장", "고추장", "쌈장",
                        "소스", "건조", "말린", "분말", "스팀", "훈제", "냉동", "가공", "버터떡")) {
            return ProductCategory.PROCESSED;
        }

        // 4. AGRICULTURAL — 농산물 명시 키워드 + 산나물류 보강
        if (containsAny(name, "시금치", "마늘", "쌀", "현미", "사과", "배", "감",
                        "고구마", "감자", "파프리카", "양파", "당근", "버섯",
                        "채소", "과일", "산지직송", "농산",
                        "두릅", "곰취", "봄나물", "산나물", "나물")) {
            return ProductCategory.AGRICULTURAL;
        }

        // catch-all (Step 3 phase 에서 ETC 신설 검토 예정)
        return ProductCategory.AGRICULTURAL;
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
