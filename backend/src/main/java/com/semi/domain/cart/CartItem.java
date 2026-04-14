package com.semi.domain.cart;

import com.semi.domain.member.Member;
import com.semi.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 장바구니 아이템 엔티티
 * 회원이 발주 전 담아두는 상품 목록
 */
@Entity
@Table(
    name = "cart_item",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_cart_member_product",
        columnNames = {"member_id", "product_id"}  // 동일 상품 중복 방지
    )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public CartItem(Member member, Product product, Integer quantity) {
        this.member    = member;
        this.product   = product;
        this.quantity  = quantity;
        this.createdAt = LocalDateTime.now();
    }

    /** 수량 변경 */
    public void updateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        this.quantity = quantity;
    }
}
