package com.semi.domain.product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final TrendKeywordRepository trendKeywordRepository;

    /** 매핑 상품을 찾을 때까지 일자를 역방향으로 탐색하는 최대 일수 */
    private static final int CURATION_FALLBACK_MAX_DAYS = 14;
    /** 한 키워드가 큐레이션을 독식하지 못하도록 키워드당 노출 상품 상한 */
    private static final int CURATION_PRODUCTS_PER_KEYWORD = 2;
    /** 큐레이션을 채택할 일자의 최소 distinct 키워드 수 (이 이상 매핑돼야 다양성 확보로 인정) */
    private static final int CURATION_MIN_DISTINCT_KEYWORDS = 10;

    /** 큐레이션 뷰 — 상단 키워드 카드와 베스트 큐레이션이 동일한 기준일/키워드 집합을 공유하기 위한 묶음 */
    public record CurationView(LocalDate baseDate, List<TrendKeyword> keywords, List<Product> products) {
        public static CurationView empty() {
            return new CurationView(null, Collections.emptyList(), Collections.emptyList());
        }
    }

    /**
     * 최신 일자부터 역방향으로 탐색해 매핑 상품이 존재하는 첫 일자의 (키워드 Top20 + 그 키워드 매핑 상품 productLimit개) 묶음 반환.
     * 키워드 카드 섹션과 베스트 큐레이션이 같은 데이터에서 파생되도록 보장.
     */
    @Transactional(readOnly = true)
    public CurationView getCuratedView(int productLimit) {
        LocalDateTime latest = trendKeywordRepository.findMaxCollectedAt();
        if (latest == null) {
            return CurationView.empty();
        }
        Pageable perKeywordPage = PageRequest.of(0, CURATION_PRODUCTS_PER_KEYWORD);
        LocalDate cursor = latest.toLocalDate();
        int distinctThreshold = Math.min(CURATION_MIN_DISTINCT_KEYWORDS, productLimit / CURATION_PRODUCTS_PER_KEYWORD);

        for (int offset = 0; offset < CURATION_FALLBACK_MAX_DAYS; offset++) {
            LocalDate target = cursor.minusDays(offset);
            LocalDateTime start = target.atStartOfDay();
            LocalDateTime end = start.plusDays(1);
            List<TrendKeyword> keywords =
                    trendKeywordRepository.findTop20KeywordsCollectedBetweenOrderById(start, end);
            if (keywords.isEmpty()) {
                continue;
            }

            // 키워드당 상한을 두고 골고루 수집해 한 키워드 독식을 방지
            List<Product> all = new ArrayList<>();
            for (TrendKeyword keyword : keywords) {
                List<Product> chunk = productRepository.findCuratedByKeywordIds(
                        List.of(keyword.getId()), perKeywordPage);
                all.addAll(chunk);
            }

            long distinctKeywords = all.stream()
                    .map(p -> p.getKeyword().getId())
                    .distinct()
                    .count();
            if (distinctKeywords < distinctThreshold) {
                continue;
            }

            List<Product> products = all.size() > productLimit ? all.subList(0, productLimit) : all;
            log.info("[curation] view base={} keywords={} distinct={} products={}",
                    target, keywords.size(), distinctKeywords, products.size());
            return new CurationView(target, keywords, products);
        }
        log.info("[curation] view no diverse data within {} days from {}", CURATION_FALLBACK_MAX_DAYS, cursor);
        return CurationView.empty();
    }

    /**
     * 최신 트렌드 키워드 배치(Top20)에 속한 상품을 큐레이션해서 반환.
     * 최신 일자에 매핑 상품이 없으면 일자를 역방향으로 최대 {@link #CURATION_FALLBACK_MAX_DAYS}일까지 탐색.
     * 키워드 rank → 이미지 유무 → 가격 ASC 정렬. limit 만큼만.
     * 끝까지 매핑 0건이면 빈 리스트 (Hard Rule: 결측 시 보간 금지).
     */
    @Transactional(readOnly = true)
    public List<Product> getCuratedProductsByLatestKeywords(int limit) {
        LocalDateTime latest = trendKeywordRepository.findMaxCollectedAt();
        if (latest == null) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        LocalDate cursor = latest.toLocalDate();
        for (int offset = 0; offset < CURATION_FALLBACK_MAX_DAYS; offset++) {
            LocalDate target = cursor.minusDays(offset);
            LocalDateTime start = target.atStartOfDay();
            LocalDateTime end = start.plusDays(1);
            List<TrendKeyword> keywords =
                    trendKeywordRepository.findTop20KeywordsCollectedBetweenOrderById(start, end);
            if (keywords.isEmpty()) {
                continue;
            }
            List<Long> keywordIds = keywords.stream().map(TrendKeyword::getId).toList();
            List<Product> result = productRepository.findCuratedByKeywordIds(keywordIds, pageable);
            if (!result.isEmpty()) {
                log.info("[curation] hit offset={} date={} products={}", offset, target, result.size());
                return result;
            }
        }
        log.info("[curation] no products within {} days from {}", CURATION_FALLBACK_MAX_DAYS, cursor);
        return Collections.emptyList();
    }

    @Transactional
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    @Transactional
    public Product getProductDetail(Long id){
        return productRepository.findProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public List<Product> searchProductsByNameOrDescription(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        String trimmedKeyword = keyword.trim();
        return productRepository.searchByNameDescriptionOrKeyword(trimmedKeyword);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return productRepository.findByNameContainingIgnoreCase(keyword.trim());
    }

    /**
     * 모든 상품의 카테고리를 현재 ProductCategoryClassifier 룰 기반으로 재분류한다.
     * 분류 룰 변경 후 기존 row 보정 용도. 변경된 row 만 saveAll 에 포함.
     */
    @Transactional
    public ReclassifyResult reclassifyAllCategories() {
        List<Product> all = productRepository.findAll();
        Map<ProductCategory, Long> before = countByCategory(all);

        List<Product> changed = new ArrayList<>();
        for (Product product : all) {
            ProductCategory previous = product.getCategory();
            product.reclassifyCategory();
            if (previous != product.getCategory()) {
                changed.add(product);
            }
        }
        if (!changed.isEmpty()) {
            productRepository.saveAll(changed);
        }

        Map<ProductCategory, Long> after = countByCategory(all);
        log.info("[reclassify] total={}, updated={}, before={}, after={}",
                all.size(), changed.size(), before, after);
        return new ReclassifyResult(all.size(), changed.size(), before, after);
    }

    private Map<ProductCategory, Long> countByCategory(List<Product> products) {
        Map<ProductCategory, Long> counts = new EnumMap<>(ProductCategory.class);
        for (ProductCategory category : ProductCategory.values()) {
            counts.put(category, 0L);
        }
        for (Product product : products) {
            ProductCategory category = product.getCategory();
            if (category != null) {
                counts.merge(category, 1L, Long::sum);
            }
        }
        return counts;
    }

    public record ReclassifyResult(
            int total,
            int updated,
            Map<ProductCategory, Long> before,
            Map<ProductCategory, Long> after
    ) {
    }
}
