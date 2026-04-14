package com.semi.domain.member.dto;

import com.semi.domain.member.MemberRole;
import jakarta.validation.constraints.NotNull;

/**
 * 회원 권한 변경 요청 DTO
 */
public record UpdateRoleRequest(

        @NotNull(message = "변경할 권한을 입력해주세요.")
        MemberRole role
) {}
