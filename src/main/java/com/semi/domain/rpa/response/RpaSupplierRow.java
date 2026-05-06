package com.semi.domain.rpa.response;

import java.time.LocalDateTime;

public record RpaSupplierRow(
    Long id,
    String name,
    String url,
    LocalDateTime createdAt
) {
}
