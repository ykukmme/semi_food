package com.semi.domain.member.dto;

/**
 * 로그인 응답 DTO — Access Token 반환
 */
public record LoginResponse(
        String accessToken,
        String tokenType,
        String role
) {
    public static LoginResponse of(String accessToken, String role) {
        return new LoginResponse(accessToken, "Bearer", role);
    }
}
