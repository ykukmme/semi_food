# semi_food RPA TODO 리스트

이 문서는 `semi_food` 프로젝트의 RPA 구현 요구사항을 기능별 구현 단위로 나눈 기준 문서입니다.

## 0. 기준 정보
- 이 문서는 구현 아이디어 메모가 아니라, 실제 코딩 작업을 위한 요구사항 정리 문서다.
- RPA 상태의 기준은 `src/main/resources/db/migration/V6__create_rpa_log_and_audit_tables.sql`이다.
- `rpa_log.status` 값은 `RUNNING`, `COMPLETED`, `FAILED`를 사용한다.
- `rpa_log`의 주요 컬럼은 `started_at`, `ended_at`, `keyword_count`, `product_count`, `message`다.
- RPA 로그 파일 저장 위치는 `src/main/resources/static/test/rpa/log`다.
- 로그 파일명 규칙은 `rpa_parsing_yymmdd_time.log`다.
- 예시 URL과 예시 파라미터 값은 실제 구현 기준이 아니라 샘플이다.

## 1. 기능 단위: RPA 파싱 준비
### 1-1. 트렌드 키워드 저장 API 연결
- 트렌드 키워드 저장 API를 호출한다.
- 예시 URL은 `http://localhost:8080/api/TrendKeywords/saveWithSequentialId`다.
- 예시 파라미터는 `size = 20`이다.

### 1-2. 저장 결과 식별
- 저장된 트렌드 키워드에서 `keywordId`, `rankId`, `syncDate`를 얻을 수 있어야 한다.
- 최근 저장된 20건을 기준으로 다음 단계가 진행되어야 한다.

### 1-3. 당일 데이터 기준 정렬
- 당일 파싱한 `TrendKeyword` 목록만 대상으로 삼는다.
- 실행 시점의 날짜와 트렌드 순위를 함께 기록한다.
- 제품과 공급자 데이터는 실행 시점 기준의 최신 데이터를 사용한다.

## 2. 기능 단위: 공급자/상품 파싱 실행
### 2-1. 공급자 저장 호출
- 최근 저장된 `TrendKeyword` 20건을 기준으로 공급자 저장 API를 반복 호출한다.
- 예시 URL은 `http://localhost:8080/api/Products/saveWithSequentialId?keywordId=160&rankId=2179193963&syncDate=20260428`다.

### 2-2. 상품 저장 호출
- 공급자 저장과 같은 흐름으로 상품 저장까지 연결한다.

### 2-3. 반복 처리 규칙
- 반복 시 조합할 값은 `keywordId`, `rankId`, `syncDate`다.
- `TrendKeyword -> Supplier -> Product` 순서로 처리한다.
- 하드코딩된 값은 예시로만 사용하고, 실제 구현에서는 동적으로 조회한다.

### 2-4. 중단 재개 기준
- 중단 재개를 고려해 각 단계의 진행 상황을 기록한다.
- 당일 파싱한 `TrendKeyword` 목록을 기준으로 반복문을 돌린다.

## 3. 기능 단위: 로그 저장
### 3-1. DB 로그 저장
- DB에는 `message`를 통해 어떤 데이터가 저장되었는지 요약 기록한다.
- `rpa_log`의 `status`, `started_at`, `ended_at`, `keyword_count`, `product_count`를 함께 사용한다.

### 3-2. 파일 로그 저장
- 파일 로그에는 RPA 실행 시점, 저장된 데이터, 디버깅 로그를 상세히 기록한다.
- 로그 파일 저장 위치는 `src/main/resources/static/test/rpa/log`다.
- 로그 파일명 규칙은 `rpa_parsing_yymmdd_time.log`다.

### 3-3. 로그 역할 분리
- DB 로그는 운영 조회와 상태 추적용으로 사용한다.
- 파일 로그는 디버깅과 상세 추적용으로 사용한다.

## 4. 기능 단위: CRUD 안전장치
### 4-1. 조회 메서드
- `trend_keyword`, `supplier`, `product` 각각에 대한 당일 기준 조회 메서드를 구현한다.
- 예시: `findAllByCreatedAtGreaterThanEqual(LocalDateTime start)`

### 4-2. 삭제 메서드
- 당일 기준 삭제 메서드를 구현한다.
- 예시: `deleteByCreatedAtGreaterThanEqual(LocalDateTime start)`

### 4-3. 삭제 순서
- 종속성을 고려해 `product -> supplier -> trend_keyword` 순서로 삭제한다.

### 4-4. 수정/삭제 차단
- `RUNNING` 상태일 때는 수정/삭제 요청을 막는 가드 로직을 구현한다.

### 4-5. 파싱과의 연결
- 파싱 순서는 `trend_keyword -> supplier -> product` 순서로 유지한다.

## 5. 기능 단위: 관리자 대시보드
### 5-1. 화면 생성
- `admin` 로그인 시에만 보이는 `rpa/dashboard.html` 화면을 구현한다.
- 동적 페이지를 기본으로 하고 CRUD API로 데이터를 조회한다.

### 5-2. 뷰 분리
- 한 화면에 4개의 뷰를 둔다.
  - `trend_keyword`
  - `supplier`
  - `product`
  - `rpa_log`
- 각 뷰는 설정한 날짜 범위의 데이터만 보여준다.
- 각 뷰 내부는 스크롤 가능해야 한다.

### 5-3. 버튼 제어
- 각 뷰 상단에 기본 CRUD 버튼을 둔다.
- `rpa_log.status`가 `RUNNING`일 때는 수정/삭제 버튼을 잠근다.
- 강제 잠금 해제(Unlock) 버튼을 제공한다.

### 5-4. 실시간 반영
- SSE(Server-Sent Events)로 RPA 실행 중 로그를 실시간 갱신한다.
- SSE(Server-Sent Events)로 DB 반영 데이터를 실시간 갱신한다.

## 6. 기능 단위: 비동기와 동시성 제어
### 6-1. 비동기 처리
- `@Async`를 사용해 웹 요청과 실제 RPA 실행을 분리한다.

### 6-2. 동시성 제어
- 중복 실행 방지를 위해 낙관적 락(`@Version`)을 검토한다.

### 6-3. 종료 보장
- `try-finally` 구조를 사용해 성공/실패와 관계없이 종료 처리를 보장한다.

### 6-4. 재시도
- 외부 사이트 호출은 실패 가능성이 있으므로 재시도 전략을 둔다.

## 7. 기능 단위: 복구 및 고도화
### 7-1. 자동 복구
- `@Scheduled`를 사용해 장시간 `RUNNING` 상태인 데이터를 찾아 복구한다.

### 7-2. 재시도 강화
- 외부 사이트의 일시적 오류에 대비해 3회 내외 재시도 로직을 둔다.

### 7-3. 알림 확장
- 실패 알림은 메일 또는 슬랙 같은 관리자 채널로 확장 가능하게 설계한다.

### 7-4. 이력 관리
- 알림 이력과 재처리 이력을 남길 수 있는 구조를 검토한다.

## 8. 완료 기준
1. `rpa_log` 기준 상태와 로그 구조가 확정되어야 한다.
2. 파싱 RPA 시퀀스가 먼저 동작해야 한다.
3. CRUD 안전 장치가 적용되어야 한다.
4. 관리자 대시보드가 구현되어야 한다.
5. SSE 실시간 반영과 알림 기능은 후순위로 확장한다.

## 9. 구현 시 주의사항
- 예시 URL의 `localhost`, `keywordId`, `rankId`, `syncDate` 값은 샘플일 뿐이며, 실제 구현은 동적 값 기준으로 작성한다.
- 문서에 나온 모든 항목을 한 번에 완성하려고 하지 말고, 실제 구현 순서대로 나눠서 진행한다.
- 이 문서는 설계 방향을 정리한 것이므로, 실제 코드 작성 시에는 현재 엔티티와 마이그레이션 스키마를 우선한다.

