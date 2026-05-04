package com.semi.controller;

import com.semi.domain.order.PurchaseOrderService;
import com.semi.domain.product.Product;
import com.semi.domain.product.ProductService;
import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordService;
import com.semi.security.MemberDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
    
    
    @GetMapping("/view")
    public String viewProductDetail(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam("id") String id,
            Model model
    ) {
        if (memberDetails == null) {
            return "redirect:/login.html";
        }

        Product product = productService.getProductDetail(Long.parseLong(id));
        System.out.println(product.toString());
        model.addAttribute("product", product);
        return "product";
    }

    @GetMapping("/dashboard_search_result")
    public String dashboardSearchResult(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam(name = "q", required = false) String query,
            Model model
    ) {
        if (memberDetails == null) {
            return "redirect:/login.html";
        }

        String trimmedQuery = query == null ? "" : query.trim();
        List<Product> products = productService.searchProductsByNameOrDescription(trimmedQuery);
        List<OrderSearchRow> orderResults = purchaseOrderService
                .searchOrderedItems(memberDetails.getMember().getId(), trimmedQuery)
                .stream()
                .map(OrderSearchRow::from)
                .toList();
        model.addAttribute("query", trimmedQuery);
        model.addAttribute("products", products);
        model.addAttribute("orderResults", orderResults);
        return "dashboard_search_result";
    }

    public record OrderSearchRow(
            String orderNumber,
            String productName,
            String statusLabel,
            Integer price,
            Integer quantity,
            Integer subtotal,
            String imageUrl
    ) {
        private static OrderSearchRow from(PurchaseOrderItem item) {
            PurchaseOrder order = item.getPurchaseOrder();
            Product product = item.getProduct();
            return new OrderSearchRow(
                    order.getOrderNumber(),
                    item.getProductName(),
                    statusLabel(order.getStatus()),
                    item.getPrice(),
                    item.getQuantity(),
                    item.subtotal(),
                    product == null ? "" : product.getImageUrl()
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
