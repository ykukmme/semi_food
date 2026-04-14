package com.semi.domain.member;

/**
 * 회원 권한 등급
 * USER  - 일반 회원 (기본값)
 * ADMIN - 관리자 (다른 회원에게 권한 부여 가능)
 */
public enum MemberRole {
    USER,
    ADMIN
}
