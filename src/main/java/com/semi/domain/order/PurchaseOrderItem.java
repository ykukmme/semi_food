package com.semi.domain.order;

import com.semi.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 발주 상품 명세 엔티티
 * 발주서 PDF 항목의 원본 데이터
 * 상품명·가격은 발주 시점 스냅샷으로 저장 (이후 상품 정보 변경과 무관)
 */
@Entity
@Table(name = "purchase_order_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private PurchaseOrder purchaseOrder;  // 'order'는 JPQL 예약어라 purchaseOrder로 명명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;  // 발주 시점 상품명 스냅샷

    @Column(nullable = false)
    private Integer price;  // 발주 시점 단가 스냅샷

    @Column(nullable = false)
    private Integer quantity;

    @Builder
    public PurchaseOrderItem(PurchaseOrder purchaseOrder, Product product,
                              String productName, Integer price, Integer quantity) {
        this.purchaseOrder = purchaseOrder;
        this.product       = product;
        this.productName = productName;
        this.price       = price;
        this.quantity    = quantity;
    }

    /** 소계 계산 */
    public int subtotal() {
        return this.price * this.quantity;
    }

    /** Get description for AdminOrderController */
    public String getDescription() {
        return product != null ? product.getDescription() : null;
    }

    /** Get unit price for AdminOrderController */
    public Integer getUnitPrice() {
        return this.price;
    }

    /** Get total price for AdminOrderController */
    public Integer getTotalPrice() {
        return subtotal();
    }
}
