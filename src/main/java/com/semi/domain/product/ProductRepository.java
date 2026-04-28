package com.semi.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /** 키워드별 상품 조회 */
    List<Product> findByKeywordId(Long keywordId);

    /** 자동발주 활성 상품 조회 */
    List<Product> findByAutoOrderTrue();

    /** ID에 해당하는 상품 단건 조회 */
    Optional<Product> findProductById(Long id);

    /** 상품명 또는 설명으로 검색 */
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    List<Product> findByNameContainingIgnoreCase(String name);

    
}
