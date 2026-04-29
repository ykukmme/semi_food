package com.semi.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCategoryClassifierTest {

    @Test
    @DisplayName("상품명에 선물/세트 키워드가 있으면 선물세트로 분류한다")
    void classifyGift() {
        assertThat(ProductCategoryClassifier.classify("남해 특산물 선물세트"))
                .isEqualTo(ProductCategory.GIFT);
    }

    @Test
    @DisplayName("상품명에 가공 키워드가 있으면 가공품으로 분류한다")
    void classifyProcessed() {
        assertThat(ProductCategoryClassifier.classify("남해 마늘즙"))
                .isEqualTo(ProductCategory.PROCESSED);
    }

    @Test
    @DisplayName("상품명에 수산물 키워드가 있으면 수산물로 분류한다")
    void classifyMarine() {
        assertThat(ProductCategoryClassifier.classify("남해 대형 전복"))
                .isEqualTo(ProductCategory.MARINE);
    }

    @Test
    @DisplayName("상품명에 농산물 키워드가 있으면 농산물로 분류한다")
    void classifyAgricultural() {
        assertThat(ProductCategoryClassifier.classify("남해 봄 시금치"))
                .isEqualTo(ProductCategory.AGRICULTURAL);
    }
}
