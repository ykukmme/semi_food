package com.semi.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /** 키워드별 상품 조회 */
    List<Product> findByKeywordId(Long keywordId);

    /** 자동발주 활성 상품 조회 */
    List<Product> findByAutoOrderTrue();

    /** 성능 최적화 상품 목록 조회 - N+1 문제 해결 */
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.keyword " +
           "LEFT JOIN FETCH p.supplier " +
           "ORDER BY " +
           "CASE WHEN p.imageUrl IS NOT NULL AND p.imageUrl != '' THEN 0 ELSE 1 END, " +
           "p.name")
    List<Product> findAllOptimized();

    /** 페이징 상품 목록 조회 - ID만 조회 후 N+1 해결 */
    @Query("SELECT p FROM Product p " +
           "ORDER BY " +
           "CASE WHEN p.imageUrl IS NOT NULL AND p.imageUrl != '' THEN 0 ELSE 1 END, " +
           "p.name")
    org.springframework.data.domain.Page<Product> findProductsPaged(org.springframework.data.domain.Pageable pageable);

    /** 제품명 또는 설명으로 검색 */
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    /** 제품명 또는 설명으로 페이징 검색 */
    org.springframework.data.domain.Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, org.springframework.data.domain.Pageable pageable);
}
