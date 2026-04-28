package com.semi.security;

import com.semi.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 생성·검증·파싱
 */
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    /** 서명 키 — 애플리케이션 시작 시 한 번만 생성해 캐시 */
    private SecretKey secretKey;

    @PostConstruct
    void init() {
        String secret = jwtProperties.getSecret();
        // HMAC-SHA256 최소 요구 길이 32바이트 검증
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "JWT secret must be at least 32 bytes. Check JWT_SECRET environment variable.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 인증 정보로 Access Token 생성 */
    public String generateToken(Authentication authentication) {
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpirySeconds() * 1000L);

        return Jwts.builder()
                .subject(memberDetails.getUsername())
                .claim("role", memberDetails.getMember().getRole().name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /** 토큰 유효성 검증 (서명·만료 확인) */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** 토큰에서 memberId(subject) 추출 */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
