package com.semi.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);

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

    @Query("""
            select product
            from Product product
            left join fetch product.keyword
            left join fetch product.supplier
            order by product.id desc
            """)
    List<Product> findAllOptimized();

    @Query(
            value = """
                    select product
                    from Product product
                    order by product.id desc
                    """,
            countQuery = """
                    select count(product)
                    from Product product
                    """
    )
    Page<Product> findProductsPaged(Pageable pageable);

    
}
