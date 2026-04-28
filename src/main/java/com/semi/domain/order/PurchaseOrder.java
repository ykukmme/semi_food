package com.semi.domain.order;

import com.semi.domain.member.Member;
import com.semi.domain.supplier.Supplier;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 발주 엔티티
 * 1건의 발주 = 1구매처 (발주서 PDF 1장 기준)
 * 수동 발주: 사용자 확인 후 생성 (is_auto = false)
 * 자동 발주: auto_order=true 상품에 한해 자동 생성 (is_auto = true)
 */
@Entity
@Table(name = "purchase_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 20)
    private String orderNumber;  // PO-YYYYMMDD-NNNN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 발주자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;  // 구매처

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;  // 총 가격 (원)

    @Column(name = "shipping_fee", nullable = false)
    private Integer shippingFee;  // 배송비 (원)

    @Column(name = "is_auto", nullable = false)
    private Boolean isAuto;  // 자동발주 여부

    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;  // shipping_address

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;  // payment_method

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;  // payment_status

    @Column(name = "subtotal", nullable = false)
    private Integer subtotal;  // subtotal

    @Column(name = "ordered_at", nullable = false, updatable = false)
    private LocalDateTime orderedAt;  // 발주일시

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @Builder
    public PurchaseOrder(String orderNumber, Member member, Supplier supplier,
                         Integer totalPrice, Integer shippingFee, Boolean isAuto,
                         Integer subtotal, String shippingAddress, String paymentMethod, String paymentStatus) {
        this.orderNumber  = orderNumber;
        this.member       = member;
        this.supplier     = supplier;
        this.totalPrice   = totalPrice;
        this.shippingFee  = shippingFee;
        this.isAuto       = isAuto != null ? isAuto : false;
        this.subtotal     = subtotal != null ? subtotal : totalPrice; // fallback if null
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.status       = OrderStatus.RECEIVED;  // 기본값: 발주 접수 완
        this.orderedAt    = LocalDateTime.now();
    }

    /** 발주 상태 변경 */
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    /** 발주 아이템 추가 */
    public void addItem(PurchaseOrderItem item) {
        this.items.add(item);
    }
}
