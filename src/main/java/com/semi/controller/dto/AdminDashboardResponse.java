package com.semi.controller.dto;

import java.util.List;

public record AdminDashboardResponse(
    long totalProducts,
    long totalMembers,
    long totalSales,
    long totalRevenue,
    String rpaStatus,
    List<KeywordItem> keywords,
    List<OrderItem> recentOrders,
    List<ProductItem> recentProducts,
    List<MemberItem> recentMembers
) {
    public record KeywordItem(
        Long id,
        int rank,
        String name,
        int frequency,
        int percentage
    ) {}

    public record OrderItem(
        String orderId,
        String product,
        String source,
        String status,
        String time
    ) {}

    public record ProductItem(
        Long id,
        String name,
        int price,
        int stock,
        String status
    ) {}

    public record MemberItem(
        Long id,
        String name,
        String email,
        String role,
        String lastLogin
    ) {}
}
