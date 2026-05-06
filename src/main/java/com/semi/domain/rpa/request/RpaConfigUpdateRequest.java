package com.semi.domain.rpa.request;

public record RpaConfigUpdateRequest(
    boolean enabled,
    String runTimes
) {
}
