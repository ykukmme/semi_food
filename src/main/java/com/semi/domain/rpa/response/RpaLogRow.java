package com.semi.domain.rpa.response;

import java.time.LocalDateTime;

public record RpaLogRow(
    Long id,
    String status,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    Integer keywordCount,
    Integer productCount,
    String message
) {
}
