# 회원가입 기능 구현 플랜

설계 승인일: 2026-04-10
기반 설계: docs/plans/2026-04-10-member-registration-design (인라인 승인)

## 구현 범위

- DB 스키마: member 테이블
- Backend: 회원가입 API, 관리자 권한 부여 API
- Frontend: 회원가입 폼
- 테스트: MemberService 단위 테스트

---

## Task 1: 프로젝트 기반 파일 생성
파일: backend/build.gradle.kts, backend/settings.gradle.kts
변경: Spring Boot 3.3.5 + Java 21 기반 Gradle 설정 생성
의존: 없음

## Task 2: DB 마이그레이션 파일
파일: backend/src/main/resources/db/migration/V1__create_member_table.sql
변경: member 테이블 DDL (id, member_id, password, email, phone, name, role, created_at)
의존: Task 1

## Task 3: Member 엔티티 + Enum
파일: backend/src/main/java/com/semi/domain/member/Member.java
파일: backend/src/main/java/com/semi/domain/member/MemberRole.java
변경: @Entity Member 클래스, MemberRole ENUM (USER, ADMIN)
의존: Task 2

## Task 4: MemberRepository
파일: backend/src/main/java/com/semi/domain/member/MemberRepository.java
변경: JpaRepository 상속, existsByMemberId(), existsByEmail() 메서드
의존: Task 3

## Task 5: DTO 클래스
파일: backend/src/main/java/com/semi/domain/member/dto/RegisterRequest.java
파일: backend/src/main/java/com/semi/domain/member/dto/MemberResponse.java
변경: 요청/응답 DTO, @Valid 어노테이션
의존: Task 3

## Task 6: MemberService
파일: backend/src/main/java/com/semi/domain/member/MemberService.java
변경: register() — 중복 체크 + BCrypt 암호화 + 저장
      updateRole() — 관리자가 권한 부여
의존: Task 4, Task 5

## Task 7: AuthController
파일: backend/src/main/java/com/semi/controller/AuthController.java
변경: POST /api/auth/register 엔드포인트
의존: Task 6

## Task 8: AdminController
파일: backend/src/main/java/com/semi/controller/AdminController.java
변경: PUT /api/admin/members/{id}/role 엔드포인트
의존: Task 6

## Task 9: GlobalExceptionHandler
파일: backend/src/main/java/com/semi/exception/GlobalExceptionHandler.java
변경: 중복 ID/이메일, 유효성 검사 실패 시 한국어 오류 응답
의존: Task 7, Task 8

## Task 10: application.yml
파일: backend/src/main/resources/application.yml
변경: TiDB Cloud 연결 설정 (환경변수 참조), JPA 설정, Flyway 설정
의존: Task 1

## Task 11: 메인 애플리케이션 클래스
파일: backend/src/main/java/com/semi/SemiApplication.java
변경: @SpringBootApplication 진입점
의존: Task 1

## Task 12: MemberService 단위 테스트
파일: backend/src/test/java/com/semi/domain/member/MemberServiceTest.java
변경: register 성공, 중복 ID 실패, 중복 이메일 실패 테스트
의존: Task 6

## Task 13: 회원가입 프론트엔드
파일: frontend/register.html
파일: frontend/js/register.js
변경: 가입 폼 UI + API 호출 JS
의존: Task 7
