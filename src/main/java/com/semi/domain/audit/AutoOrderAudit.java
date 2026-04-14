package com.semi.domain.audit;

import com.semi.domain.member.Member;
import com.semi.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 자동발주 플래그 변경 이력 엔티티
 * Hard Rule: product.auto_order 변경 시 반드시 이 테이블에 기록
 */
@Entity
@Table(name = "auto_order_audit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoOrderAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private Member changedBy;  // 변경한 관리자

    @Column(name = "old_value", nullable = false)
    private Boolean oldValue;  // 변경 전 값

    @Column(name = "new_value", nullable = false)
    private Boolean newValue;  // 변경 후 값

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(length = 200)
    private String reason;  // 변경 사유

    @Builder
    public AutoOrderAudit(Product product, Member changedBy,
                           Boolean oldValue, Boolean newValue, String reason) {
        this.product   = product;
        this.changedBy = changedBy;
        this.oldValue  = oldValue;
        this.newValue  = newValue;
        this.reason    = reason;
        this.changedAt = LocalDateTime.now();
    }
}
