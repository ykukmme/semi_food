package com.semi.domain.rpa.response;

import java.time.LocalDateTime;

public record RpaProductRow(
    Long id,
    Long keywordId,
    String keyword,
    Long supplierId,
    String supplierName,
    String name,
    Integer price,
    Boolean autoOrder,
    LocalDateTime crawledAt,
    LocalDateTime syncDate
) {
}
