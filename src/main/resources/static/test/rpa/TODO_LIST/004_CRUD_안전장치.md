# 004 CRUD 안전장치

## 목적
- RPA 실행 중 데이터 수정/삭제로 인한 무결성 문제를 방지한다.

## 1. 조회 메서드
- `trend_keyword`, `supplier`, `product` 각각에 대한 당일 기준 조회 메서드를 구현한다.
- 예시: `findAllByCreatedAtGreaterThanEqual(LocalDateTime start)`
- `TrendKeywordRepository.findAllByCollectedAtGreaterThanEqualOrderByCollectedAtDesc()`가 추가되었다.
- `SupplierRepository.findAllByCreatedAtGreaterThanEqualOrderByCreatedAtDesc()`가 추가되었다.
- `ProductRepository.findAllByCrawledAtGreaterThanEqualOrderByCrawledAtDesc()`가 추가되었다.
- 각 엔티티의 기준 날짜 필드는 서로 다르다. `TrendKeyword.collectedAt`, `Supplier.createdAt`, `Product.crawledAt`를 사용해야 한다.
- 상품은 `Product.syncDate`도 존재하므로, RPA 실행 기준 날짜 조회에서는 `crawledAt`과 `syncDate` 중 어떤 기준을 사용할지 API별로 명확히 정해야 한다.
- `/rpa/data/daily?date=yyyy-MM-dd`로 당일 기준 데이터 건수와 삭제 가능 건수를 조회할 수 있다.

## 2. 삭제 메서드
- 당일 기준 삭제 메서드를 구현한다.
- 예시: `deleteByCreatedAtGreaterThanEqual(LocalDateTime start)`
- 직접 조건 삭제 대신 삭제 가능 엔티티 목록을 조회한 뒤 서비스 계층에서 `deleteAllInBatch()`를 실행한다.
- `/rpa/data/daily?date=yyyy-MM-dd&confirm=true` DELETE 요청으로 삭제한다.
- `confirm=true`가 없으면 삭제하지 않는다.

## 3. 삭제 순서
- 종속성을 고려해 `product -> supplier -> trend_keyword` 순서로 삭제한다.
- 발주 상품 명세에 연결된 상품은 삭제 대상에서 제외한다.
- 상품 또는 발주에 연결된 공급자는 삭제 대상에서 제외한다.
- 남아 있는 상품이 참조하는 트렌드 키워드는 삭제 대상에서 제외한다.

## 4. 수정/삭제 차단
- `RUNNING` 상태일 때는 수정/삭제 요청을 막는 가드 로직을 구현한다.
- 현재 `RpaLogRepository.findTopByOrderByStartedAtDesc()`로 최신 RPA 상태 조회는 가능하다.
- `RpaCrudSafetyService`가 추가되어 메모리 실행 상태와 최신 `RpaLog.status`를 함께 확인한다.
- RPA 실행 중 삭제 요청은 `IllegalStateException`으로 차단된다.

## 5. 파싱과의 연결
- 파싱 순서는 `trend_keyword -> supplier -> product` 순서로 유지한다.

## 완료 기준
- 당일 기준 조회와 삭제가 가능하다.
- `RUNNING` 상태에서는 위험한 수정/삭제가 차단된다.
- 삭제는 서비스 계층에서 `product -> supplier -> trend_keyword` 순서로 처리된다.

## 추가 구현 필요
- 현재 삭제 API의 예외 응답은 공통 에러 포맷으로 정리되어 있지 않다.
- `Product.autoOrder` 변경은 `auto_order_audit` 기록 규칙과 충돌하지 않게 별도 서비스에서 처리해야 한다.
- 공급자는 `createdAt` 기준으로 조회하되, 실제 삭제는 상품/발주에 연결되지 않은 고아 공급자만 대상으로 한다.
- 다중 서버 환경에서는 `RUNNING` 판정에 DB 기반 잠금 정책을 추가 검토해야 한다.

## 이번 단계 구현 결과
- `RpaCrudSafetyService`를 추가했다.
- `RpaDailyDataService`를 추가했다.
- `RpaDailyDataSummary`, `RpaDailyDeleteResponse`를 추가했다.
- `/rpa/data/daily` GET/DELETE API를 추가했다.
- 발주/상품 FK를 고려해 삭제 가능한 데이터만 삭제하도록 Repository 조회를 추가했다.
