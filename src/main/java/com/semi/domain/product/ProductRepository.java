package com.semi.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /** 키워드별 상품 조회 */
    List<Product> findByKeywordId(Long keywordId);

    /** 자동발주 활성 상품 조회 */
    List<Product> findByAutoOrderTrue();


    // 서버에서 ID를 수동처리하기 위한 코드
    @Query("SELECT MAX(t.id) FROM Product t")
    Long findMaxId();
    // 서버에서 ID를 수동처리하기 위한 코드
    // 중복 체크용 (데이터가 이미 있는지 확인)
    boolean existsByCrawledAtAndName(LocalDateTime crawledAt, String name);

    Product findFirstByOrderByIdDesc();

}
