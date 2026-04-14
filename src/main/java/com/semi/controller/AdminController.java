package com.semi.controller;

import com.semi.domain.member.MemberService;
import com.semi.domain.member.dto.MemberResponse;
import com.semi.domain.member.dto.UpdateRoleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    /**
     * 회원 권한 변경 (관리자 전용)
     * PUT /api/admin/members/{id}/role
     * 요청 본문: { "role": "ADMIN" }
     */
    @PutMapping("/members/{id}/role")
    public ResponseEntity<MemberResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        MemberResponse response = memberService.updateRole(id, request.role());
        return ResponseEntity.ok(response);
    }
}
