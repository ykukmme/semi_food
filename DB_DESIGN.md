# DB 설계 문서

> 이 문서는 스마트 식품 이커머스 플랫폼의 데이터베이스 설계 의도를 설명합니다.
> 코드를 처음 보는 사람도 "왜 이렇게 만들었는지"를 이해할 수 있도록 작성했습니다.

---

## 1. 전체 흐름 먼저 이해하기

이 서비스가 하는 일을 한 문장으로 요약하면:

> **"RPA가 네이버에서 트렌드 식품을 자동 수집 → 사용자가 마음에 드는 상품을 골라 발주 → 발주서 자동 생성"**

이 흐름을 따라가면 DB 구조가 자연스럽게 이해됩니다.

```
[RPA 수집]         [사용자 행동]         [발주 처리]
트렌드 키워드  →   상품 목록 조회   →   장바구니 담기  →  발주  →  발주서 PDF
공급업체 등록      (대시보드 확인)       수량 결정          상태 추적
```

---

## 2. 테이블 목록 한눈에 보기

| 테이블 | 한 줄 설명 | 누가 만드나 |
|--------|-----------|------------|
| `member` | 서비스 이용자 (로그인 계정) | 회원가입 시 |
| `trend_keyword` | RPA가 수집한 트렌드 키워드 | RPA 자동 |
| `supplier` | 상품을 파는 공급업체 | RPA 자동 |
| `product` | 크롤링된 상품 정보 | RPA 자동 |
| `cart_item` | 사용자의 장바구니 | 사용자 |
| `purchase_order` | 발주 내역 (발주서 헤더) | 사용자 |
| `purchase_order_item` | 발주서에 담긴 상품 목록 | 발주 시 자동 |
| `rpa_log` | RPA가 언제 돌았는지 기록 | RPA 자동 |
| `auto_order_audit` | 자동발주 설정 변경 이력 | 관리자 |

---

## 3. 테이블별 설계 의도

---

### member — 회원

```
id, member_id, password, email, phone, name, role, created_at
```

**왜 이렇게?**

- `member_id`는 로그인 아이디 (닉네임처럼 사용자가 직접 정함)
- `password`는 BCrypt로 해시 저장 — 원문은 DB에 절대 저장하지 않음
- `role`은 `USER` / `ADMIN` 두 가지만 존재
  - 일반 사용자는 상품 조회 + 발주만 가능
  - 관리자는 회원 관리 + 자동발주 설정 가능

---

### trend_keyword — 트렌드 키워드

```
id, keyword, rank, frequency, collected_at, is_active
```

**왜 이렇게?**

- RPA가 6시간마다 네이버 쇼핑 식품 카테고리 1~10위 키워드를 수집
- `rank`: 몇 위인지 (1~10)
- `frequency`: 이 키워드가 얼마나 자주 등장했는지 (대시보드 그래프용)
- `collected_at`: 언제 수집됐는지 — 시간이 지난 데이터와 최신 데이터를 구분하기 위해
- `is_active`: 새로 수집하면 기존 키워드를 비활성화(false)하고 새 키워드를 활성(true)으로 설정
  - 삭제하지 않고 비활성화하는 이유: 과거 트렌드 이력을 보존하기 위해

---

### supplier — 공급업체

```
id, name, url, created_at
```

**왜 이렇게?**

- RPA가 상품을 크롤링할 때 판매자(공급업체) 정보도 함께 수집
- 발주서에 "구매처"가 반드시 기재되어야 해서 별도 테이블로 분리
- `name`에 UNIQUE 제약을 걸어서 같은 공급업체가 중복 등록되지 않도록 함
  - RPA가 동일한 공급업체를 두 번 수집해도 이미 있으면 그냥 기존 것을 씀

---

### product — 상품

```
id, keyword_id, supplier_id, name, description, price, image_url, product_url, auto_order, crawled_at
```

**왜 이렇게?**

- `keyword_id`: 어떤 트렌드 키워드로 수집된 상품인지 추적
  - "닭가슴살" 키워드로 수집된 상품인지, "그래놀라" 키워드로 수집된 건지 알 수 있음
- `supplier_id`: 이 상품을 어느 공급업체에서 파는지
- `product_url`: 네이버 쇼핑 원본 링크 — 발주 시 참고용
- `auto_order`: **자동발주 여부** (기본값 `false`)
  - 이 값이 `true`인 상품은 주문이 들어오면 자동으로 발주가 나감
  - 기본은 반드시 `false` — 실수로 자동발주가 되는 것을 방지
  - 변경 시 반드시 `auto_order_audit`에 이력을 남겨야 함 (아래 참고)
- `crawled_at`: 데이터가 언제 수집됐는지 — 오래된 가격 정보를 걸러내기 위해

---

### cart_item — 장바구니

```
id, member_id, product_id, quantity, created_at
```

**왜 이렇게?**

- 장바구니는 "어떤 회원이 어떤 상품을 몇 개 담았는지"만 기록하면 됨
- `UNIQUE(member_id, product_id)`: 동일한 상품을 두 번 담으면 새 행을 추가하는 게 아니라 수량만 수정하도록 강제
  - 예: 닭가슴살을 1개 담고 또 1개 담으면 → 수량 2개로 변경 (행은 1개)

---

### purchase_order — 발주

```
id, order_number, member_id, supplier_id, status, total_price, shipping_fee, is_auto, ordered_at
```

**왜 이렇게?**

- **발주 1건 = 공급업체 1곳**
  - 발주서 1장에는 구매처가 1곳만 기재됨
  - 장바구니에 A공급업체 상품과 B공급업체 상품이 섞여 있으면 → 발주가 2건 생성됨
- `order_number`: `PO-20260414-0001` 형식의 발주번호 — 사람이 읽기 쉽도록
- `status`: 발주의 현재 상태
  ```
  RECEIVED(접수) → IN_PROGRESS(진행중) → SHIPPED(출고완료) → COMPLETED(처리완료)
                                                              CANCELLED(취소) ← RECEIVED에서만 가능
  ```
- `is_auto`: 자동발주로 생성된 건지, 사람이 직접 발주한 건지 구분
- **`supplier_id`를 직접 저장하는 이유**: 발주 당시의 공급업체 정보를 고정시키기 위해

---

### purchase_order_item — 발주 상품 명세

```
id, order_id, product_id, product_name, price, quantity
```

**왜 이렇게?**

- 발주서에 들어가는 상품 목록
- `product_name`, `price`를 **스냅샷**으로 저장하는 것이 핵심
  - 발주 이후 상품 가격이 바뀌거나 상품이 삭제되더라도
  - 발주서에는 **당시의 상품명과 가격**이 그대로 보존됨
  - 예: 발주 당시 3,000원이었는데 나중에 5,000원으로 바뀌어도 발주서는 3,000원으로 유지

---

### rpa_log — RPA 실행 로그

```
id, status, started_at, ended_at, keyword_count, product_count, message
```

**왜 이렇게?**

- 대시보드에서 "RPA 현재 가동 중인지" 표시하기 위해
  - 가장 최근 로그의 `status`가 `RUNNING`이면 → 가동 중
  - `COMPLETED`이면 → 정상 완료, `FAILED`이면 → 오류 상태
- `keyword_count`, `product_count`: 이번 수집에서 몇 개나 가져왔는지 기록
- `message`: 오류가 났을 때 어떤 오류인지 저장

---

### auto_order_audit — 자동발주 플래그 변경 이력

```
id, product_id, changed_by, old_value, new_value, changed_at, reason
```

**왜 이렇게?**

- `auto_order` 플래그는 **돈과 직결**되는 설정임
  - 이 값이 `true`가 되면 발주가 자동으로 나가서 실제 비용이 발생
- 그래서 누가, 언제, 왜 바꿨는지 반드시 기록해야 함
- `changed_by`: 변경한 관리자 계정 (책임 추적)
- `old_value` / `new_value`: 변경 전/후 값 (false→true인지, true→false인지)
- 이 테이블은 **삭제/수정 없이 추가만** 하는 append-only 로그

---

## 4. 핵심 설계 원칙 3가지

### 원칙 1. 스냅샷 저장

발주서의 `product_name`, `price`는 상품 테이블을 참조하지 않고 **값을 복사해서 저장**합니다.

```
나쁜 설계: 발주서 → 상품 테이블 참조 (상품 가격이 바뀌면 발주서도 바뀜)
좋은 설계: 발주서에 당시 가격을 직접 저장 (발주 이후 변경 불가)
```

### 원칙 2. 비활성화, 삭제하지 않는다

키워드나 상품은 삭제하지 않고 `is_active = false`로 비활성화합니다.

```
이유: 과거 발주 내역에서 "이 상품이 왜 발주됐는지" 추적할 수 있어야 함
     삭제하면 발주 이력의 근거가 사라짐
```

### 원칙 3. 자동발주는 기본 OFF + 변경 이력 필수

```
auto_order 기본값: false
변경 절차: 관리자 승인 → auto_order_audit 기록 → product.auto_order 변경
```

돈이 나가는 기능은 실수 방지를 위해 가장 보수적으로 설계합니다.

---

## 5. 테이블 간 관계 요약

```
member ──────────────────────────── cart_item ──── product ──── trend_keyword
  │                                                   │
  │                                                supplier
  │                                                   │
  └──── purchase_order ──── purchase_order_item       │
              │                    └─────────────── product
           supplier

product ──── auto_order_audit ──── member (관리자)
rpa_log (독립 — 다른 테이블과 연결 없음)
```

---

## 6. Flyway 마이그레이션 순서

| 파일 | 내용 |
|------|------|
| V1 | member 테이블 생성 |
| V2 | trend_keyword 테이블 생성 |
| V3 | supplier + product 테이블 생성 |
| V4 | cart_item 테이블 생성 |
| V5 | purchase_order + purchase_order_item 테이블 생성 |
| V6 | rpa_log + auto_order_audit 테이블 생성 |
| V7 | status 컬럼 ENUM → VARCHAR 변경 (확장성) |

> V3에서 product가 supplier와 trend_keyword를 참조하므로, V2보다 반드시 나중에 실행되어야 합니다.
> Flyway가 버전 번호 순서대로 자동 실행하므로 직접 신경 쓸 필요는 없습니다.
