package com.semi.domain.rpa.parser.response;

public record RpaSupplierProductParseResult(
    Long keywordId,
    Long rankId,
    String syncDate,
    Integer trendRank,
    String status,
    Integer productCount,
    String message
) {
}
