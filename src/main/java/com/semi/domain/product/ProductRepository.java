package com.semi.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    /** 트렌드 키워드 큐레이션 — 주어진 키워드 ID 집합에 속한 미삭제 상품. 키워드 rank → 이미지 유무 → 가격 순.
     *  현재 RPA가 stock/available_stock을 채우지 않아 가용재고 필터는 임시 미적용. */
    @Query("""
            SELECT p FROM Product p
            LEFT JOIN FETCH p.keyword k
            LEFT JOIN FETCH p.supplier
            WHERE k.id IN :keywordIds
              AND p.delDate IS NULL
            ORDER BY
              k.rank ASC,
              CASE WHEN p.imageUrl IS NOT NULL AND p.imageUrl <> '' THEN 0 ELSE 1 END,
              p.price ASC
            """)
    List<Product> findCuratedByKeywordIds(
            @Param("keywordIds") List<Long> keywordIds,
            org.springframework.data.domain.Pageable pageable);

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

    /** ID에 해당하는 상품 단건 조회 */
    Optional<Product> findProductById(Long id);

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("""
            select product
            from Product product
            left join product.keyword keyword
            where lower(product.name) like lower(concat('%', :query, '%'))
               or lower(coalesce(product.description, '')) like lower(concat('%', :query, '%'))
               or lower(coalesce(keyword.keyword, '')) like lower(concat('%', :query, '%'))
            order by product.id desc
            """)
    List<Product> searchByNameDescriptionOrKeyword(@Param("query") String query);

//     @Query("""
//             select product
//             from Product product
//             left join fetch product.keyword
//             left join fetch product.supplier
//             order by product.id desc
//             """)
//     List<Product> findAllOptimized();

//     @Query(
//             value = """
//                     select product
//                     from Product product
//                     order by product.id desc
//                     """,
//             countQuery = """
//                     select count(product)
//                     from Product product
//                     """
//     )
//     Page<Product> findProductsPaged(Pageable pageable);

    
}
