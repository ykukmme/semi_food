package com.semi.domain.order.dto;

public record OrderItemResponse(
    Long id,
    String productName,
    String description,
    Integer quantity,
    Integer unitPrice,
    Integer totalPrice
) {}
