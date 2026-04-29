package com.semi.domain.product;

import java.util.Locale;

public final class ProductCategoryClassifier {

    private ProductCategoryClassifier() {
    }

    public static ProductCategory classify(String productName) {
        String name = normalize(productName);

        if (containsAny(name, "선물", "세트", "기프트", "박스", "명절")) {
            return ProductCategory.GIFT;
        }

        if (containsAny(name, "즙", "잼", "청", "장아찌", "장", "소스", "건조", "말린", "분말","답례품", "팩", "종", "버터떡", "스팀", "훈제", "냉동", "가공")) {
            return ProductCategory.PROCESSED;
        }

        if (containsAny(name, "전복", "멸치", "김", "미역", "다시마", "굴", "쭈꾸미", "새우", "문어", "오징어", "생선", "갈치", "고등어", "수산")) {
            return ProductCategory.MARINE;
        }

        if (containsAny(name, "시금치", "마늘", "쌀", "현미", "사과", "배", "감", "고구마", "감자", "파프리카", "채소", "과일", "산지직송", "농산")) {
            return ProductCategory.AGRICULTURAL;
        }

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
