package com.semi.domain.rpa.response;

import java.time.LocalDate;

public record RpaDailyDataSummary(
    LocalDate targetDate,
    Integer trendKeywordCount,
    Integer productCount,
    Integer supplierCount,
    Integer deletableTrendKeywordCount,
    Integer deletableProductCount,
    Integer deletableSupplierCount,
    Boolean rpaRunning,
    String message
) {
}
