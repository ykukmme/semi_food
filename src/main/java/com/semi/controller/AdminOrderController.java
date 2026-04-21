package com.semi.controller;

import com.semi.domain.order.PurchaseOrder;
import com.semi.domain.order.PurchaseOrderRepository;
import com.semi.domain.order.OrderStatus;
import com.semi.domain.order.dto.OrderResponse;
import com.semi.domain.order.dto.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final PurchaseOrderRepository purchaseOrderRepository;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = purchaseOrderRepository.findAll().stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        OrderResponse response = convertToOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    private OrderResponse convertToOrderResponse(PurchaseOrder order) {
        // Convert order items
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductName(),
                        item.getDescription(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        // Convert status
        OrderResponse.OrderStatus status = convertStatus(order.getStatus());

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getMember().getName(),
                order.getMember().getEmail(),
                order.getMember().getPhone(),
                order.getShippingAddress(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                status,
                order.getOrderedAt(),
                order.getSubtotal(),
                order.getShippingFee(),
                order.getTotalPrice(),
                items
        );
    }

    private OrderResponse.OrderStatus convertStatus(OrderStatus status) {
        try {
            return OrderResponse.OrderStatus.valueOf(status.name());
        } catch (IllegalArgumentException e) {
            return OrderResponse.OrderStatus.PROCESSING; // Default status
        }
    }
}
