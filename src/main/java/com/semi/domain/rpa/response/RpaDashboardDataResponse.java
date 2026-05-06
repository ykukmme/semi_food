package com.semi.domain.rpa.response;

import java.time.LocalDate;
import java.util.List;

public record RpaDashboardDataResponse(
    LocalDate targetDate,
    RpaDailyDataSummary summary,
    List<RpaTrendKeywordRow> trendKeywords,
    List<RpaSupplierRow> suppliers,
    List<RpaProductRow> products,
    List<RpaLogRow> rpaLogs
) {
}
