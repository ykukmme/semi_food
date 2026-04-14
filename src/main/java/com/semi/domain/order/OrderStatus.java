package com.semi.domain.order;

/**
 * 발주 상태
 * RECEIVED    - 발주 접수 완
 * IN_PROGRESS - 진행중
 * SHIPPED     - 출고 완료
 * COMPLETED   - 처리 완료
 * CANCELLED   - 발주 취소 (RECEIVED 단계에서만 가능)
 */
public enum OrderStatus {
    RECEIVED,
    IN_PROGRESS,
    SHIPPED,
    COMPLETED,
    CANCELLED
}
