package com.semi.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /** 아이디 중복 확인 */
    boolean existsByMemberId(String memberId);

    /** 이메일 중복 확인 */
    boolean existsByEmail(String email);

    /** 로그인 시 회원 조회 */
    Optional<Member> findByMemberId(String memberId);
}
