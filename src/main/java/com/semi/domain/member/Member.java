package com.semi.domain.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 엔티티
 * 로그인 ID, 비밀번호(BCrypt), 이메일, 전화번호, 이름, 권한을 저장한다.
 */
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false, unique = true, length = 50)
    private String memberId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "`role`", nullable = false, length = 10)  // role은 MySQL 8.0 예약어 — 백틱 필수
    private MemberRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Member(String memberId, String password, String email,
                  String phone, String name) {
        this.memberId  = memberId;
        this.password  = password;
        this.email     = email;
        this.phone     = phone;
        this.name      = name;
        this.role      = MemberRole.USER; // 기본값: 일반 회원
        this.createdAt = LocalDateTime.now();
    }

    /** 관리자가 권한을 변경할 때 사용 */
    public void updateRole(MemberRole newRole) {
        this.role = newRole;
    }

    /** 비밀번호 변경 시 사용 */
    public void updatePassword(String password) {
        this.password = password;
    }

    /** ID 설정 시 사용 (하드코딩용) */
    public void setId(Long id) {
        this.id = id;
    }

    /** 역할 설정 시 사용 (하드코딩용) */
    public void setRole(MemberRole role) {
        this.role = role;
    }
}
