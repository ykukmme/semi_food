package com.semi.domain.member.dto;

import com.semi.domain.member.Member;
import com.semi.domain.member.MemberRole;

import java.time.LocalDateTime;

/**
 * 회원 응답 DTO — 비밀번호는 절대 포함하지 않음
 */
public record MemberResponse(
        Long id,
        String memberId,
        String email,
        String phone,
        String name,
        MemberRole role,
        LocalDateTime createdAt
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getMemberId(),
                member.getEmail(),
                member.getPhone(),
                member.getName(),
                member.getRole(),
                member.getCreatedAt()
        );
    }
}
