package com.semi.domain.product.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateAutoOrderRequest(
    @NotNull(message = "Auto order status is required")
    Boolean autoOrder,
    
    String reason
) {}
