package com.semi.domain.member;

import com.semi.domain.member.dto.MemberResponse;
import com.semi.domain.member.dto.RegisterRequest;
import com.semi.exception.DuplicateMemberException;
import com.semi.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * - 아이디/이메일 중복 체크 후 BCrypt 해시 저장
     */
    @Transactional
    public MemberResponse register(RegisterRequest request) {
        // 아이디 중복 확인
        if (memberRepository.existsByMemberId(request.memberId())) {
            throw new DuplicateMemberException("이미 사용 중인 아이디입니다.");
        }
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateMemberException("이미 사용 중인 이메일입니다.");
        }

        Member member = Member.builder()
                .memberId(request.memberId())
                .password(passwordEncoder.encode(request.password()))  // BCrypt 해시
                .email(request.email())
                .phone(request.phone())
                .name(request.name())
                .build();

        return MemberResponse.from(memberRepository.save(member));
    }

    /**
     * 관리자가 특정 회원의 권한을 변경
     */
    @Transactional
    public MemberResponse updateRole(Long memberId, MemberRole newRole) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));
        member.updateRole(newRole);
        return MemberResponse.from(member);
    }
}
