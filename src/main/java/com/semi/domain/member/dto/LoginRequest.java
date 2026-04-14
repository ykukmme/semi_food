package com.semi.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO
 */
public record LoginRequest(

        @NotBlank(message = "아이디를 입력해주세요.")
        String memberId,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {}
