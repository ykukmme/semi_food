package com.semi.domain.product;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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
