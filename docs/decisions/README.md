# Architecture Decision Records

이 프로젝트의 주요 설계 결정을 기록합니다.
새 의존성 추가, 기존 패턴 교체, 데이터 모델 변경, 구조 재편 시 반드시 항목을 추가하세요.

## 템플릿

```markdown
# [결정 제목]
## Context: 왜 이 결정이 필요한가
## Decision: 무엇을 선택했는가
## Consequences: 트레이드오프, 알려진 제약
```

---

## Decisions

### ADR-001: 초기 기술 스택 결정
**Context**: `/project-init` 인터뷰를 통해 프로젝트 시작 전 스택과 규칙을 확정.

**Decision**:
- Frontend: HTML / CSS / JavaScript (바닐라) — 별도 프레임워크 없이 Spring Boot static 서빙
- Backend: Java 21 + Spring Boot 4.0.5 — REST API 및 비즈니스 로직
- RPA/분석: Python — 네이버 쇼핑 크롤링 및 트렌드 분석
- DB: TiDB Cloud (MySQL 호환) — 스케일 가능한 분산 DB, MySQL 드라이버 재사용
- 배포: AWS EC2 (예정) — 로컬 개발 후 단일 서버 배포

**Consequences**:
- Java + Python 혼용으로 두 런타임 관리 필요 (빌드 파이프라인 분리)
- TiDB Cloud는 MySQL 호환이지만 일부 MySQL 전용 기능(특정 DDL 구문 등) 확인 필요
- 바닐라 JS는 초기 개발 속도는 빠르나 복잡도 증가 시 프레임워크 도입 검토 필요

---

### ADR-002: 자동발주 플래그 설계
**Context**: 발주 자동화는 금전 손실 가능성이 있어 기본적으로 수동 확인이 필요하지만,
운영 효율을 위해 품목별 자동발주 옵션을 제공해야 함.

**Decision**:
- 발주는 기본적으로 사용자 확인 후 실행 (Hard Rule #2)
- 상품 테이블에 `auto_order BOOLEAN DEFAULT FALSE` 컬럼 추가
- 해당 플래그가 `true`인 품목만 확인 단계 건너뜀
- 플래그 변경 및 자동발주 실행 이력은 별도 audit 테이블에 로깅

**Consequences**:
- 자동발주 오발주 시 추적 가능 (audit log)
- 플래그 활성화 UI/UX는 의도적으로 불편하게 설계 — 실수 방지
- 추후 품목 그룹 단위 플래그 관리로 확장 가능
