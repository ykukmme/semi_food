package com.semi.domain.rpa.response;

import java.time.LocalDate;

public record RpaDailyDeleteResponse(
    LocalDate targetDate,
    Integer deletedProductCount,
    Integer deletedSupplierCount,
    Integer deletedTrendKeywordCount,
    String message
) {
}
