package com.semi.test;

import com.semi.domain.member.MemberRepository;
import com.semi.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 로그인 디버깅용 임시 컨트롤러
 * 개발 환경에서만 사용
 */
@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class LoginDebugController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * admin 비밀번호 테스트
     */
    @PostMapping("/test-password")
    public Map<String, Object> testPassword(@RequestParam String testPassword) {
        Map<String, Object> result = new HashMap<>();
        
        // admin 계정 조회
        Member admin = memberRepository.findByMemberId("admin")
                .orElse(null);
        
        if (admin == null) {
            result.put("status", "error");
            result.put("message", "admin 계정을 찾을 수 없습니다.");
            return result;
        }
        
        // 현재 저장된 해시
        String storedHash = admin.getPassword();
        
        // 테스트 비밀번호 해시
        String testHash = passwordEncoder.encode(testPassword);
        
        // 해시 비교
        boolean matches = passwordEncoder.matches(testPassword, storedHash);
        
        result.put("status", "success");
        result.put("testPassword", testPassword);
        result.put("storedHash", storedHash);
        result.put("testHash", testHash);
        result.put("matches", matches);
        result.put("passwordLength", testPassword.length());
        
        return result;
    }

    /**
     * admin 계정 정보 조회
     */
    @GetMapping("/admin-info")
    public Map<String, Object> getAdminInfo() {
        Map<String, Object> result = new HashMap<>();
        
        Member admin = memberRepository.findByMemberId("admin")
                .orElse(null);
        
        if (admin == null) {
            result.put("status", "error");
            result.put("message", "admin 계정을 찾을 수 없습니다.");
            return result;
        }
        
        result.put("status", "success");
        result.put("memberId", admin.getMemberId());
        result.put("email", admin.getEmail());
        result.put("role", admin.getRole().name());
        result.put("passwordHash", admin.getPassword());
        result.put("createdAt", admin.getCreatedAt());
        
        return result;
    }
}
