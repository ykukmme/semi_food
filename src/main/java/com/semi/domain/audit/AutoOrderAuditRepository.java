package com.semi.domain.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutoOrderAuditRepository extends JpaRepository<AutoOrderAudit, Long> {

    /** 상품별 변경 이력 조회 (최신순) */
    List<AutoOrderAudit> findByProductIdOrderByChangedAtDesc(Long productId);
}
