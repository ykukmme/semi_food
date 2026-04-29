package com.semi.controller;

import com.semi.domain.order.PurchaseOrder;
import com.semi.domain.order.PurchaseOrderService;
import com.semi.domain.order.dto.CreatePurchaseOrderRequest;
import com.semi.domain.order.dto.PurchaseOrderResponse;
import com.semi.security.MemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createOrder(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody CreatePurchaseOrderRequest request
    ) {
        if (memberDetails == null) {
            return ResponseEntity.status(401).build();
        }

        PurchaseOrder order = purchaseOrderService.createOrder(memberDetails.getMember().getId(), request);
        return ResponseEntity.ok(PurchaseOrderResponse.from(order));
    }

    @PostMapping("/{orderNumber}/cancel")
    public ResponseEntity<PurchaseOrderResponse> cancelOrder(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable String orderNumber
    ) {
        if (memberDetails == null) {
            return ResponseEntity.status(401).build();
        }

        PurchaseOrder order = purchaseOrderService.cancelOrder(memberDetails.getMember().getId(), orderNumber);
        return ResponseEntity.ok(PurchaseOrderResponse.from(order));
    }
}
