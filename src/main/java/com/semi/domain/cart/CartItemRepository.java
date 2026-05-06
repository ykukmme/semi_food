package com.semi.domain.cart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /** 회원의 장바구니 전체 조회 */
    @EntityGraph(attributePaths = "product")
    List<CartItem> findByMemberId(Long memberId);

    @EntityGraph(attributePaths = "product")
    List<CartItem> findTop5ByMemberIdOrderByCreatedAtDesc(Long memberId);

    /** 특정 상품이 장바구니에 있는지 확인 */
    Optional<CartItem> findByMemberIdAndProductId(Long memberId, Long productId);

    /** 발주 완료 후 장바구니 비우기 */
    @Transactional
    void deleteByMemberId(Long memberId);

    /** 회원 장바구니의 특정 상품 삭제 */
    @Transactional
    void deleteByMemberIdAndProductId(Long memberId, Long productId);
}
