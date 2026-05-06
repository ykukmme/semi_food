package com.semi.domain.order;

import com.semi.domain.cart.CartItemRepository;
import com.semi.domain.member.Member;
import com.semi.domain.member.MemberRepository;
import com.semi.domain.order.dto.CreatePurchaseOrderRequest;
import com.semi.domain.product.Product;
import com.semi.domain.product.ProductRepository;
import com.semi.domain.supplier.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
    @DisplayName("주문 생성 후에도 장바구니 항목은 삭제하지 않는다")
    void createOrder_keepsCartItems() {
        // given
        Long memberId = 7L;
        Long productId = 10L;
        Member member = Member.builder()
                .memberId("buyer")
                .password("password")
                .email("buyer@example.com")
                .name("buyer")
                .build();
        Supplier supplier = Supplier.builder()
                .name("supplier")
                .url("https://example.com")
                .build();
        Product product = Product.builder()
                .supplier(supplier)
                .name("남해 마늘")
                .description("fresh")
                .price(5000)
                .imageUrl("")
                .productUrl("")
                .crawledAt(LocalDateTime.now())
                .stock(10)
                .availableStock(10)
                .build();
        CreatePurchaseOrderRequest request = new CreatePurchaseOrderRequest(
                List.of(new CreatePurchaseOrderRequest.Item(productId, 2)),
                0,
                "Gyeongsangnam-do, Namhae-gun",                            // 3. shippingAddress (추가)
                "EASY_PAY",                                                // 4. paymentMethod (추가)
                "PENDING"
                
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(purchaseOrderRepository.save(any(PurchaseOrder.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        purchaseOrderService.createOrder(memberId, request);

        // then
        then(cartItemRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("주문 취소 시 주문 상태와 결제 상태를 함께 변경한다")
    void cancelOrder_updatesOrderStatusAndPaymentStatus() {
        // given
        Long memberId = 7L;
        String orderNumber = "PO-20260506-0001";
        Member member = Member.builder()
                .memberId("buyer")
                .password("password")
                .email("buyer@example.com")
                .name("buyer")
                .build();
        Supplier supplier = Supplier.builder()
                .name("supplier")
                .url("https://example.com")
                .build();
        PurchaseOrder order = PurchaseOrder.builder()
                .orderNumber(orderNumber)
                .member(member)
                .supplier(supplier)
                .totalPrice(10000)
                .shippingFee(0)
                .isAuto(false)
                .subtotal(10000)
                .shippingAddress("Gyeongsangnam-do, Namhae-gun")
                .paymentMethod("EASY_PAY")
                .paymentStatus("COMPLETED")
                .build();

        given(purchaseOrderRepository.findByMemberIdAndOrderNumber(memberId, orderNumber))
                .willReturn(Optional.of(order));
        given(purchaseOrderRepository.updateCancelAndPaymentStatus(
                memberId,
                orderNumber,
                OrderStatus.CANCELLED.name(),
                "REFUND"
        )).willAnswer(invocation -> {
            order.updateStatus(OrderStatus.CANCELLED);
            order.updatePaymentStatus("REFUND");
            return 1;
        });
        given(purchaseOrderRepository.findByMemberIdAndOrderNumber(memberId, orderNumber))
                .willReturn(Optional.of(order));

        // when
        PurchaseOrder cancelledOrder = purchaseOrderService.cancelOrder(memberId, orderNumber);

        // then
        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(cancelledOrder.getPaymentStatus()).isEqualTo("REFUND");
    }

    @Test
    @DisplayName("이미 취소된 주문도 결제 상태를 환불로 보정한다")
    void cancelOrder_updatesPaymentStatusWhenOrderAlreadyCancelled() {
        // given
        Long memberId = 7L;
        String orderNumber = "PO-20260506-0002";
        Member member = Member.builder()
                .memberId("buyer")
                .password("password")
                .email("buyer@example.com")
                .name("buyer")
                .build();
        Supplier supplier = Supplier.builder()
                .name("supplier")
                .url("https://example.com")
                .build();
        PurchaseOrder order = PurchaseOrder.builder()
                .orderNumber(orderNumber)
                .member(member)
                .supplier(supplier)
                .totalPrice(10000)
                .shippingFee(0)
                .isAuto(false)
                .subtotal(10000)
                .shippingAddress("Gyeongsangnam-do, Namhae-gun")
                .paymentMethod("EASY_PAY")
                .paymentStatus("COMPLETED")
                .build();
        order.updateStatus(OrderStatus.CANCELLED);

        given(purchaseOrderRepository.findByMemberIdAndOrderNumber(memberId, orderNumber))
                .willReturn(Optional.of(order));
        given(purchaseOrderRepository.updateCancelAndPaymentStatus(
                memberId,
                orderNumber,
                OrderStatus.CANCELLED.name(),
                "REFUND"
        )).willAnswer(invocation -> {
            order.updatePaymentStatus("REFUND");
            return 1;
        });
        given(purchaseOrderRepository.findByMemberIdAndOrderNumber(memberId, orderNumber))
                .willReturn(Optional.of(order));

        // when
        PurchaseOrder cancelledOrder = purchaseOrderService.cancelOrder(memberId, orderNumber);

        // then
        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(cancelledOrder.getPaymentStatus()).isEqualTo("REFUND");
    }

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
