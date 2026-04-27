package com.semi.domain.product;

import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.supplier.Supplier;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 상품 엔티티
 * RPA가 트렌드 키워드 기준으로 크롤링한 상품 (키워드당 2개)
 */
@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private TrendKeyword keyword;  // 수집 출처 키워드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;  // 공급업체

    @Column(nullable = false, length = 200)
    private String name;  // 상품명

    @Column(columnDefinition = "TEXT")
    private String description;  // 상품 설명

    @Column(nullable = false)
    private Integer price;  // 가격 (원)

    @Column(name = "image_url", length = 500)
    private String imageUrl;  // 이미지 주소

    @Column(name = "product_url", length = 500)
    private String productUrl;  // 상품 구매처 링크

    @Column(name = "auto_order", nullable = false)
    private Boolean autoOrder;  // 자동발주 플래그 (기본 OFF, 변경 시 audit 필수)

    @Column(name = "stock", nullable = false)
    private Integer stock;  // 재고 수량

    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;  // 주문 가능 수량

    @Column(name = "crawled_at", nullable = false)
    private LocalDateTime crawledAt;  // 크롤링 시간

    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @Column(name = "mod_date")
    private LocalDateTime modDate;

    @Column(name = "del_date")
    private LocalDateTime delDate;

    @Builder
    public Product(TrendKeyword keyword, Supplier supplier, String name, String description,
                   Integer price, String imageUrl, String productUrl, LocalDateTime crawledAt,
                   Integer stock, Integer availableStock) {
        this.keyword     = keyword;
        this.supplier    = supplier;
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.imageUrl    = imageUrl;
        this.productUrl  = productUrl;
        this.autoOrder   = false;  // 자동발주 기본 OFF (Hard Rule)
        this.stock       = stock != null ? stock : 0;
        this.availableStock = availableStock != null ? availableStock : this.stock;
        this.crawledAt   = crawledAt;
    }

    /**
     * 자동발주 플래그 변경
     * !! 반드시 ProductService.changeAutoOrder() 를 통해서만 호출할 것 !!
     * 직접 호출 시 AutoOrderAudit 이력이 누락되어 Hard Rule 위반이 됨.
     * ProductService.changeAutoOrder() 내에서 AutoOrderAudit 저장과 이 메서드 호출을
     * 하나의 @Transactional 블록으로 묶어 원자성을 보장해야 한다.
     */
    public void updateAutoOrder(Boolean newValue) {
        this.autoOrder = newValue;
    }

    /** 크롤링 재수집 시 정보 갱신 */
    public void updateCrawledInfo(String name, String description, Integer price,
                                   String imageUrl, String productUrl, LocalDateTime crawledAt,
                                   Integer stock, Integer availableStock) {
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.imageUrl    = imageUrl;
        this.productUrl  = productUrl;
        this.stock       = stock != null ? stock : this.stock;
        this.availableStock = availableStock != null ? availableStock : this.availableStock;
        this.crawledAt   = crawledAt;
    }

    @PrePersist
    public void prePersist() {
        this.regDate = LocalDateTime.now();
        this.modDate = this.regDate;
    }

    @PreUpdate
    public void preUpdate() {
        this.modDate = LocalDateTime.now();
    }
}
