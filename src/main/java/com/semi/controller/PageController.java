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
    public String orderDetail(
            @RequestParam(required = false) String orderNumber,
            Model model
    ) {
        if (orderNumber == null || orderNumber.isBlank()) {
            return "order_detail";
        }

        model.addAttribute("requestedOrderNumber", orderNumber);
        try {
            PurchaseOrder order = purchaseOrderService.getOrderByOrderNumber(orderNumber);
            model.addAttribute("order", OrderDetailRow.from(order));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("orderError", "주문번호 " + orderNumber + "에 해당하는 주문 정보를 찾을 수 없습니다.");
        }
        return "order_detail";
    }

    @GetMapping("/order_success")
    public String orderSuccess(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderNumber,
            Model model
    ) {
        model.addAttribute("status", status);
        if (orderNumber == null || orderNumber.isBlank()) {
            if ("fail".equals(status)) {
                model.addAttribute("orderError", "주문 저장에 실패했습니다. 잠시 후 다시 시도해주세요.");
            }
            return "order_success";
        }

        model.addAttribute("requestedOrderNumber", orderNumber);
        try {
            PurchaseOrder order = purchaseOrderService.getOrderByOrderNumber(orderNumber);
            model.addAttribute("order", OrderDetailRow.from(order));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("orderError", "주문번호 " + orderNumber + "에 해당하는 주문 정보를 찾을 수 없습니다.");
        }
        return "order_success";
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
        model.addAttribute("memberId", resolvedMemberId);
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
        model.addAttribute("memberId", resolvedMemberId);
        return "cancel_orders";
    }

    @GetMapping("/mypage")
    public String myPage(
            @AuthenticationPrincipal MemberDetails memberDetails,
            Model model
    ) {
        if (memberDetails == null) {
            return "redirect:/login.html";
        }

        model.addAttribute("member", memberDetails.getMember());
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

    public record OrderDetailRow(
            String orderNumber,
            String statusLabel,
            Integer totalPrice,
            Integer shippingFee,
            LocalDateTime orderedAt,
            List<OrderItemRow> items
    ) {
        private static OrderDetailRow from(PurchaseOrder order) {
            return new OrderDetailRow(
                    order.getOrderNumber(),
                    OrderRow.statusLabel(order.getStatus()),
                    order.getTotalPrice(),
                    order.getShippingFee(),
                    order.getOrderedAt(),
                    order.getItems().stream()
                            .map(item -> new OrderItemRow(
                                    item.getProductName(),
                                    item.getPrice(),
                                    item.getQuantity(),
                                    item.subtotal(),
                                    item.getProduct().getImageUrl()
                            ))
                            .toList()
            );
        }
    }

    public record OrderItemRow(
            String productName,
            Integer price,
            Integer quantity,
            Integer subtotal,
            String imageUrl
    ) {
    }
}
