package com.semi.domain.order;

import com.semi.domain.cart.CartItemRepository;
import com.semi.domain.member.MemberRepository;
import com.semi.domain.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    @Test
    @DisplayName("회원 재구매율은 2회 이상 구매한 상품 종류 수를 전체 구매 상품 종류 수로 나눈다")
    void getRepeatPurchaseRateByMember_usesRepeatedProductShare() {
        // given
        Long memberId = 7L;
        given(purchaseOrderItemRepository.countDistinctOrderedProductsByMemberId(memberId)).willReturn(4L);
        given(purchaseOrderItemRepository.countRepeatOrderedProductsByMemberId(memberId)).willReturn(2L);

        // when
        double repeatPurchaseRate = purchaseOrderService.getRepeatPurchaseRate(memberId);

        // then
        assertThat(repeatPurchaseRate).isEqualTo(50.0);
    }

    @Test
    @DisplayName("구매 상품이 없는 회원의 재구매율은 0이다")
    void getRepeatPurchaseRateByMember_returnsZeroWhenNoProducts() {
        // given
        Long memberId = 7L;
        given(purchaseOrderItemRepository.countDistinctOrderedProductsByMemberId(memberId)).willReturn(0L);

        // when
        double repeatPurchaseRate = purchaseOrderService.getRepeatPurchaseRate(memberId);

        // then
        assertThat(repeatPurchaseRate).isZero();
    }
}
