package com.semi.domain.rpa.response;

public record RpaConfigResponse(
    boolean enabled,
    String runTimes
) {
}
