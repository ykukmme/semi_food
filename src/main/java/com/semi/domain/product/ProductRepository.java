package com.semi.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
        SELECT COUNT(p) > 0
        FROM Product p
        WHERE p.keyword.keyword = :keyword
          AND p.keyword.rank = :rank
          AND p.name = :name
          AND p.syncDate = :syncDate
          AND p.crawledAt >= :crawledAtStart
          AND p.crawledAt < :crawledAtEnd
        """)
    boolean existsRpaProductToday(
        @Param("keyword") String keyword,
        @Param("rank") Integer rank,
        @Param("name") String name,
        @Param("syncDate") LocalDateTime syncDate,
        @Param("crawledAtStart") LocalDateTime crawledAtStart,
        @Param("crawledAtEnd") LocalDateTime crawledAtEnd
    );

    @Query("""
        SELECT COUNT(p) > 0
        FROM Product p
        WHERE p.keyword.keyword = :keyword
          AND p.keyword.rank = :rank
          AND p.syncDate = :syncDate
          AND p.crawledAt >= :crawledAtStart
          AND p.crawledAt < :crawledAtEnd
        """)
    boolean existsRpaCategoryProcessedToday(
        @Param("keyword") String keyword,
        @Param("rank") Integer rank,
        @Param("syncDate") LocalDateTime syncDate,
        @Param("crawledAtStart") LocalDateTime crawledAtStart,
        @Param("crawledAtEnd") LocalDateTime crawledAtEnd
    );

    Product findFirstByOrderByIdDesc();

    List<Product> findAllByCrawledAtGreaterThanEqualOrderByCrawledAtDesc(LocalDateTime start);

    List<Product> findAllByCrawledAtGreaterThanEqualAndCrawledAtLessThanOrderByCrawledAtDesc(
        LocalDateTime start,
        LocalDateTime end
    );

    @Query("""
        SELECT p
        FROM Product p
        WHERE p.crawledAt >= :start
          AND p.crawledAt < :end
          AND NOT EXISTS (
              SELECT item.id
              FROM PurchaseOrderItem item
              WHERE item.product = p
          )
        ORDER BY p.crawledAt DESC
        """)
    List<Product> findRpaDeletableProducts(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

}
