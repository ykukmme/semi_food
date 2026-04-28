package com.semi.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    /** 회원의 발주 목록 조회 (최신순) */
    List<PurchaseOrder> findByMemberIdOrderByOrderedAtDesc(Long memberId);

    /** 발주번호로 조회 */
    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    @Query("""
            select distinct po
            from PurchaseOrder po
            left join fetch po.items item
            left join fetch item.product
            where po.member.id = :memberId
              and po.orderNumber = :orderNumber
            """)
    Optional<PurchaseOrder> findDetailByMemberIdAndOrderNumber(
            @Param("memberId") Long memberId,
            @Param("orderNumber") String orderNumber
    );

    /** 당일 발주 수 조회 (발주번호 채번용) */
    @Query("""
            select distinct po
            from PurchaseOrder po
            left join fetch po.items item
            left join fetch item.product
            where po.orderNumber = :orderNumber
            """)
    Optional<PurchaseOrder> findDetailByOrderNumber(@Param("orderNumber") String orderNumber);

    long countByOrderedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    long countByStatus(OrderStatus status);
}
