package com.semi.controller;

import com.semi.domain.member.MemberService;
import com.semi.domain.member.dto.LoginRequest;
import com.semi.domain.member.dto.LoginResponse;
import com.semi.domain.member.dto.MemberResponse;
import com.semi.domain.member.dto.RegisterRequest;
import com.semi.security.JwtProvider;
import com.semi.security.MemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    /**
     * 회원가입
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<MemberResponse> register(@Valid @RequestBody RegisterRequest request) {
        MemberResponse response = memberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인 — JWT Access Token 발급
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.memberId(), request.password())
            );
            MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
            String token = jwtProvider.generateToken(authentication);
            return ResponseEntity.ok(LoginResponse.of(token, memberDetails.getMember().getRole().name()));
        } catch (BadCredentialsException e) {
            // 아이디/비밀번호 오류 — 구체적인 원인 노출 금지 (열거 공격 방지)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    /**
     * 내 정보 조회 — 토큰 유효성 확인용
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> me(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(MemberResponse.from(memberDetails.getMember()));
    }
}
