package com.semi.domain.rpa.parser.response;

import java.time.LocalDateTime;

public record RpaTrendKeywordParseTarget(
    Long keywordId,
    Long rankId,
    String syncDate,
    Integer trendRank,
    LocalDateTime collectedAt
) {
}
