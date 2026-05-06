package com.semi.domain.rpa.response;

public record RpaRunResponse(
    String status,
    String message,
    Integer requestedSize
) {
}
