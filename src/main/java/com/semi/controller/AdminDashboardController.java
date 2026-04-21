package com.semi.controller;

import com.semi.controller.dto.AdminDashboardResponse;
import com.semi.domain.member.MemberService;
import com.semi.domain.product.Product;
import com.semi.domain.product.ProductRepository;
import com.semi.domain.order.PurchaseOrderRepository;
import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final MemberService memberService;
    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final TrendKeywordRepository trendKeywordRepository;

    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponse> getDashboardStats() {
        // Calculate statistics
        long totalProducts = productRepository.count();
        long totalMembers = memberService.getAllMembers().size();
        long totalOrders = purchaseOrderRepository.count();
        long totalRevenue = 0; // TODO: Calculate actual revenue

        // Trend keyword data
        List<TrendKeyword> keywords = trendKeywordRepository.findByIsActiveTrueOrderByRankAsc();
        List<AdminDashboardResponse.KeywordItem> keywordItems = keywords.stream()
                .limit(10)
                .map(keyword -> {
                    // Calculate percentage based on max frequency
                    int maxFrequency = keywords.stream()
                            .mapToInt(TrendKeyword::getFrequency)
                            .max()
                            .orElse(1);
                    int percentage = maxFrequency > 0 ? (keyword.getFrequency() * 100) / maxFrequency : 0;
                    return new AdminDashboardResponse.KeywordItem(
                            keyword.getId(),
                            keyword.getRank(),
                            keyword.getKeyword(),
                            keyword.getFrequency(),
                            percentage
                    );
                })
                .toList();

        // Sample recent logs
        List<AdminDashboardResponse.OrderItem> recentLogs = List.of(
                new AdminDashboardResponse.OrderItem("#FC-29381", "Organic Cherry Tomatoes", "NAVER", "COMPLETED", "Just now"),
                new AdminDashboardResponse.OrderItem("#FC-29380", "Fresh Avocado", "KURLY", "COMPLETED", "3 min"),
                new AdminDashboardResponse.OrderItem("#FC-29379", "Premium Mango", "COUPANG", "COMPLETED", "12 min"),
                new AdminDashboardResponse.OrderItem("#FC-29378", "Mixed Salad Pack", "NAVER", "PENDING", "25 min")
        );

        return ResponseEntity.ok(new AdminDashboardResponse(
                totalProducts,
                totalMembers,
                totalOrders,
                totalRevenue,
                "ACTIVE", // RPA status
                keywordItems,
                recentLogs,
                List.of(), // TODO: Actual product data
                List.of()  // TODO: Actual member data
        ));
    }
}
