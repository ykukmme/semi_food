package com.semi.domain.rpa.response;

import java.time.LocalDateTime;

public record RpaTrendKeywordRow(
    Long id,
    String keyword,
    Integer rank,
    Integer frequency,
    LocalDateTime collectedAt,
    Boolean active,
    Long rankingId,
    LocalDateTime syncDate
) {
}
