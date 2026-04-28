# Agent Routing — 스마트 식품 이커머스 플랫폼 (프로젝트 확장)

글로벌 `~/.claude/rules/agents.md` 라우팅을 상속하며 확장.

## 프로젝트 전용 라우팅 규칙

- `발주`, `auto_order`, `자동발주` 관련 코드 변경 → security-reviewer 필수 통과 (blocking)
- `RPA`, `크롤링`, `수집기` 변경 → security-reviewer (외부 데이터 검증 확인)
- `TiDB`, `마이그레이션`, `스키마` 변경 → security-reviewer (SQL 인젝션 확인)

## auto_order 특별 규칙

발주·자동발주 관련 코드 변경 시 아래 4가지 모두 확인:
1. security-reviewer 통과 (blocking)
2. auto_order 플래그 기본값이 `false`인지 확인
3. 발주 실행 경로에 사용자 확인 단계가 존재하는지 확인
4. audit 로그 기록 코드가 포함됐는지 확인
