package com.semi.controller;

import com.semi.controller.dto.AdminDashboardResponse;
import com.semi.domain.member.MemberService;
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
        long totalRevenue = purchaseOrderRepository.findAll().stream()
                .mapToLong(order -> order.getTotalPrice() != null ? order.getTotalPrice() : 0)
                .sum();

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
                new AdminDashboardResponse.OrderItem("#FC-29381", "Organic Cherry Tomatoes", "Naver Store", "COMPLETED", "Just now"),
                new AdminDashboardResponse.OrderItem("#FC-29380", "Fresh Avocado", "Kurly", "COMPLETED", "3 min"),
                new AdminDashboardResponse.OrderItem("#FC-29379", "Premium Mango", "Coupang", "COMPLETED", "12 min"),
                new AdminDashboardResponse.OrderItem("#FC-29378", "Mixed Salad Pack", "Naver Store", "PENDING", "25 min")
        );

        // Generate RPA extraction data based on product crawling activity
        List<AdminDashboardResponse.RpaExtractionItem> rpaExtractionData = generateRpaExtractionData();

        return ResponseEntity.ok(new AdminDashboardResponse(
                totalProducts,
                totalMembers,
                totalOrders,
                totalRevenue,
                "ACTIVE", // RPA status
                keywordItems,
                recentLogs,
                List.of(), // Recent products - to be implemented
                List.of(), // Recent members - to be implemented
                rpaExtractionData
        ));
    }

    private List<AdminDashboardResponse.RpaExtractionItem> generateRpaExtractionData() {
        // Generate hourly extraction data for the past 24 hours
        // In real implementation, this would come from actual RPA extraction logs
        java.util.List<AdminDashboardResponse.RpaExtractionItem> data = new java.util.ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        for (int i = 23; i >= 0; i--) {
            java.time.LocalDateTime hourTime = now.minusHours(i);
            String hour = hourTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            
            // Simulate extraction counts based on time of day
            // Higher activity during business hours (9-18)
            int baseCount = 50;
            if (hourTime.getHour() >= 9 && hourTime.getHour() <= 18) {
                baseCount = 120 + (int)(Math.random() * 80); // 120-200 during business hours
            } else if (hourTime.getHour() >= 6 && hourTime.getHour() <= 8) {
                baseCount = 80 + (int)(Math.random() * 40); // 80-120 in morning
            } else if (hourTime.getHour() >= 19 && hourTime.getHour() <= 22) {
                baseCount = 60 + (int)(Math.random() * 40); // 60-100 in evening
            } else {
                baseCount = 20 + (int)(Math.random() * 30); // 20-50 during night
            }
            
            int successCount = (int)(baseCount * 0.85 + Math.random() * baseCount * 0.1); // 85-95% success rate
            int failureCount = baseCount - successCount;
            
            data.add(new AdminDashboardResponse.RpaExtractionItem(hour, baseCount, successCount, failureCount));
        }
        
        return data;
    }
}
