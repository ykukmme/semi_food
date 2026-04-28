package com.semi.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    /** 회원의 발주 목록 조회 (최신순) */
    List<PurchaseOrder> findByMemberIdOrderByOrderedAtDesc(Long memberId);

    /** 발주번호로 조회 */
    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    /** 당일 발주 수 조회 (발주번호 채번용) */
    long countByOrderedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    long countByStatus(OrderStatus status);
}
