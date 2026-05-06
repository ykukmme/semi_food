package com.semi.domain.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /** 이름으로 조회 (RPA 크롤링 시 중복 생성 방지용) */
    Optional<Supplier> findByName(String name);

    // 서버에서 ID를 수동처리하기 위한 코드
    @Query("SELECT MAX(t.id) FROM Supplier t")
    Long findMaxId();
    // 서버에서 ID를 수동처리하기 위한 코드
    // 중복 체크용 (데이터가 이미 있는지 확인)
    boolean existsByCreatedAtAndName(LocalDateTime createdAt, String name);
    
    Supplier findFirstByOrderByIdDesc();

    List<Supplier> findAllByCreatedAtGreaterThanEqualOrderByCreatedAtDesc(LocalDateTime start);

    List<Supplier> findAllByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
        LocalDateTime start,
        LocalDateTime end
    );

    @Query("""
        SELECT s
        FROM Supplier s
        WHERE s.createdAt >= :start
          AND s.createdAt < :end
          AND NOT EXISTS (
              SELECT p.id
              FROM Product p
              WHERE p.supplier = s
          )
          AND NOT EXISTS (
              SELECT o.id
              FROM PurchaseOrder o
              WHERE o.supplier = s
          )
        ORDER BY s.createdAt DESC
        """)
    List<Supplier> findRpaDeletableSuppliers(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

}
