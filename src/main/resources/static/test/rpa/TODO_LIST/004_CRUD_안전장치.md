# 004 CRUD 안전장치

## 목적
- RPA 실행 중 데이터 수정/삭제로 인한 무결성 문제를 방지한다.

## 1. 조회 메서드
- `trend_keyword`, `supplier`, `product` 각각에 대한 당일 기준 조회 메서드를 구현한다.
- 예시: `findAllByCreatedAtGreaterThanEqual(LocalDateTime start)`
- 현재 `TrendKeywordRepository`에는 당일 기준 조회 메서드가 없다.
- 현재 `SupplierRepository`에는 당일 기준 조회 메서드가 없다.
- 현재 `ProductRepository`에는 당일 기준 조회 메서드가 없다.
- 각 엔티티의 기준 날짜 필드는 서로 다르다. `TrendKeyword.collectedAt`, `Supplier.createdAt`, `Product.crawledAt`를 사용해야 한다.
- 상품은 `Product.syncDate`도 존재하므로, RPA 실행 기준 날짜 조회에서는 `crawledAt`과 `syncDate` 중 어떤 기준을 사용할지 API별로 명확히 정해야 한다.

## 2. 삭제 메서드
- 당일 기준 삭제 메서드를 구현한다.
- 예시: `deleteByCreatedAtGreaterThanEqual(LocalDateTime start)`
- 현재 세 Repository 모두 당일 기준 삭제 메서드가 없다.

## 3. 삭제 순서
- 종속성을 고려해 `product -> supplier -> trend_keyword` 순서로 삭제한다.

## 4. 수정/삭제 차단
- `RUNNING` 상태일 때는 수정/삭제 요청을 막는 가드 로직을 구현한다.
- 현재 `RpaLogRepository.findTopByOrderByStartedAtDesc()`로 최신 RPA 상태 조회는 가능하다.
- 현재 이 상태를 사용해 CRUD를 차단하는 서비스/컨트롤러 가드는 구현되어 있지 않다.

## 5. 파싱과의 연결
- 파싱 순서는 `trend_keyword -> supplier -> product` 순서로 유지한다.

## 완료 기준
- 당일 기준 조회와 삭제가 가능하다.
- `RUNNING` 상태에서는 위험한 수정/삭제가 차단된다.

## 추가 구현 필요
- 삭제는 반드시 서비스 계층에서 트랜잭션으로 묶고 `product -> supplier -> trend_keyword` 순서를 보장해야 한다.
- 운영 안전을 위해 실제 삭제 전에 대상 건수 조회와 관리자 확인 흐름을 두는 것이 좋다.
- `Product.autoOrder` 변경은 `auto_order_audit` 기록 규칙과 충돌하지 않게 별도 서비스에서 처리해야 한다.
- 공급자에는 `sync_date`가 없으므로 당일 삭제 시 상품 참조가 사라진 뒤 고아 공급자를 삭제할지, `createdAt` 기준으로 삭제할지 정책을 정해야 한다.
