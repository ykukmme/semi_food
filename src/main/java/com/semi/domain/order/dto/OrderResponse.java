package com.semi.domain.order.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    String orderNumber,
    String customerName,
    String customerEmail,
    String customerPhone,
    String shippingAddress,
    String paymentMethod,
    String paymentStatus,
    OrderStatus status,
    LocalDateTime orderDate,
    Integer subtotal,
    Integer shippingFee,
    Integer totalPrice,
    List<OrderItemResponse> items
) {
    public enum OrderStatus {
        PROCESSING, QUEUED, FAILED, COMPLETED, CANCELLED
    }
}
