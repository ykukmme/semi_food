package com.semi.controller;

import com.semi.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    /**
     * 임시 비밀번호 변경 API (개발 환경용)
     * admin 계정의 비밀번호를 'admin123'으로 변경
     */
    @PostMapping("/reset-admin-password")
    public ResponseEntity<String> resetAdminPassword() {
        try {
            memberService.resetAdminPassword();
            return ResponseEntity.ok("admin 비밀번호가 'admin123'으로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("비밀번호 변경 실패: " + e.getMessage());
        }
    }

    // AdminController is now empty - functionality moved to AdminMemberController
    // to avoid mapping conflicts
}
