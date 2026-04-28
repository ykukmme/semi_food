package com.semi.controller;

import com.semi.domain.order.OrderStatus;
import com.semi.domain.order.PurchaseOrder;
import com.semi.domain.order.PurchaseOrderService;
import com.semi.security.MemberDetails;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }

    @GetMapping("/order_detail")
    public String orderDetail() {
        return "order_detail";
    }

    @GetMapping("/all_orders")
    public String allOrders(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam(required = false) Long memberId,
            Model model
    ) {
        Long resolvedMemberId = memberDetails != null ? memberDetails.getMember().getId() : memberId;
        if (resolvedMemberId == null) {
            return "redirect:/login.html";
        }

        List<OrderRow> orders = purchaseOrderService.getOrdersByMemberId(resolvedMemberId).stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .map(OrderRow::from)
                .toList();
        model.addAttribute("orders", orders);
        return "all_orders";
    }

    @GetMapping("/cancel_orders")
    public String cancelOrders(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam(required = false) Long memberId,
            Model model
    ) {
        Long resolvedMemberId = memberDetails != null ? memberDetails.getMember().getId() : memberId;
        if (resolvedMemberId == null) {
            return "redirect:/login.html";
        }

        List<OrderRow> orders = purchaseOrderService.getOrdersByMemberId(resolvedMemberId).stream()
                .filter(order -> order.getStatus() == OrderStatus.CANCELLED)
                .map(OrderRow::from)
                .toList();
        model.addAttribute("orders", orders);
        return "cancel_orders";
    }

    @GetMapping("/mypage")
    public String myPage() {
        return "mypage";
    }

    public record OrderRow(
            String orderNumber,
            String statusLabel,
            Integer totalPrice,
            LocalDateTime orderedAt,
            boolean cancellable
    ) {
        private static OrderRow from(PurchaseOrder order) {
            return new OrderRow(
                    order.getOrderNumber(),
                    statusLabel(order.getStatus()),
                    order.getTotalPrice(),
                    order.getOrderedAt(),
                    order.getStatus() == OrderStatus.RECEIVED || order.getStatus() == OrderStatus.IN_PROGRESS
            );
        }

        private static String statusLabel(OrderStatus status) {
            return switch (status) {
                case RECEIVED -> "주문 접수";
                case IN_PROGRESS -> "처리 중";
                case SHIPPED -> "배송 중";
                case COMPLETED -> "주문 완료";
                case CANCELLED -> "취소 완료";
            };
        }
    }
}
