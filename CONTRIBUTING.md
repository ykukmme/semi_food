# Contributing Guide

## 브랜치 전략

```
main        — 배포 브랜치 (직접 커밋 금지)
develop     — 통합 브랜치 (PR 머지 대상)
feature/    — 새 기능 (예: feature/trend-collector)
fix/        — 버그 수정 (예: fix/order-validation)
```

- 모든 작업은 `develop`에서 브랜치를 따서 시작
- `main` 직접 푸시 금지 — PR을 통해서만 머지

## PR 규칙

1. **PR 제목**: `[타입] 간결한 설명` (예: `[feat] 네이버 쇼핑 RPA 수집기 구현`)
2. **PR 크기**: 한 PR에 하나의 논리적 변경. 리뷰 가능한 단위로 분리
3. **테스트 필수**: PR 생성 전 `./gradlew test` + `pytest rpa/tests/ -q` 통과 확인
4. **리뷰어 최소 1명** 승인 후 머지

## 커밋 컨벤션

```
feat:   새 기능
fix:    버그 수정
refactor: 동작 변경 없는 코드 개선
test:   테스트 추가/수정
docs:   문서 수정
chore:  빌드, 설정 등 기타
```

예시: `feat: 트렌드 키워드 빈도 분석 API 추가`

## 환경 설정

1. `.env.example`을 복사하여 `.env` 생성
   ```bash
   cp .env.example .env
   ```
2. `.env`에 아래 항목 입력 (값은 팀 채널에서 공유)
   - `TIDB_URL` — TiDB Cloud 접속 URL
   - `TIDB_USERNAME` / `TIDB_PASSWORD` — DB 계정
   - `JWT_SECRET` — **최소 32자 이상** 랜덤 문자열 (`openssl rand -base64 32`)
   - `JWT_EXPIRY_SECONDS` — 토큰 만료(초), 기본 7200
3. `.env`는 절대 커밋하지 않음 (.gitignore에 등록됨)

## 백엔드 실행

```bash
cd backend
./gradlew bootRun   # Windows: .\gradlew.bat bootRun
```

## DB 마이그레이션

TiDB Cloud에 수동으로 SQL 실행 (Flyway 자동 적용 전):
```
backend/src/main/resources/db/migration/V1__create_member_table.sql
```

## 자동발주 기능 관련 주의사항

- `auto_order` 플래그 관련 코드 변경 시 반드시 팀 리뷰 필수
- 발주 관련 로직은 audit 로그와 함께 구현
- 테스트 환경에서는 실제 발주가 나가지 않도록 `AUTO_ORDER_ENABLED=0` 유지
