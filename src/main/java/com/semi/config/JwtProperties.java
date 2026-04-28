package com.semi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 설정값 바인딩
 * application.yml의 jwt.secret, jwt.expiry-seconds를 환경변수로 주입받음
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 서명 키 — 최소 32자 이상의 랜덤 문자열 (환경변수 JWT_SECRET) */
    private String secret;

    /** 토큰 만료 시간(초) — 기본 7200초(2시간) */
    private long expirySeconds;
}
