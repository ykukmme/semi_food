package com.semi.domain.rpa.response;

import java.time.LocalDate;

public record RpaViewDeleteResponse(
    LocalDate targetDate,
    String viewName,
    Integer deletedCount,
    String message
) {
}
