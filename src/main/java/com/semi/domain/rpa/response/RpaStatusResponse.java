package com.semi.domain.rpa.response;

import java.time.LocalDateTime;

public record RpaStatusResponse(
    Long logId,
    String status,
    LocalDateTime startedAt,
    LocalDateTime endedAt,
    Integer keywordCount,
    Integer productCount,
    String message
) {
}
