package com.semi.controller;

import com.semi.domain.cart.CartItem;
import com.semi.domain.cart.CartItemService;
import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordService;
import com.semi.domain.order.PurchaseOrderService;
import com.semi.security.MemberDetails;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final CartItemService cartItemService;
    private final TrendKeywordService trendKeywordService;
    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public String dashboard(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam("memberId") String memberId,
            Model model
    ) {
        if (memberDetails == null) {
            return "redirect:/login.html";
        }

        String loginMemberId = memberDetails.getMember().getMemberId();
        if (!loginMemberId.equals(memberId)) {
            return "redirect:/login.html";
        }

        return buildDashboard(memberDetails, model);
    }

    @GetMapping
    public String getDashboard(
            @AuthenticationPrincipal MemberDetails memberDetails,
            Model model
    ) {
        if (memberDetails == null) {
            return "redirect:/login.html";
        }

        return buildDashboard(memberDetails, model);
    }

    private String buildDashboard(MemberDetails memberDetails, Model model) {
        List<TrendKeyword> keywords = trendKeywordService.getKeywordsOrderById();
        keywords.sort((a, b) -> a.getId().compareTo(b.getId()));

        Optional<TrendKeyword> latestKeyword = keywords.stream()
                .filter(keyword -> keyword.getCollectedAt() != null)
                .max((left, right) -> left.getCollectedAt().compareTo(right.getCollectedAt()));
        model.addAttribute("keywords", keywords);
        latestKeyword.ifPresent(keyword -> model.addAttribute("keywordCollectedAt", keyword.getCollectedAt()));

        Long memberId = memberDetails.getMember().getId();
        List<CartItem> recentCartItems = cartItemService.getRecentCartItems(memberId);
        model.addAttribute("dashboardMessage", "대시보드");
        model.addAttribute("member", memberDetails.getMember());
        model.addAttribute("recentCartItems", recentCartItems);
        model.addAttribute("totalOrderCount", purchaseOrderService.getTotalOrderCount(memberId));
        double orderCancellationRate = purchaseOrderService.getOrderCancellationRate(memberId);
        model.addAttribute("orderCancellationRate", orderCancellationRate);
        model.addAttribute("orderCancellationRateText", String.format(Locale.KOREA, "%.1f%%", orderCancellationRate));
        model.addAttribute("totalOrderedProductCount", purchaseOrderService.getTotalOrderedProductCount(memberId));
        double repeatPurchaseRate = purchaseOrderService.getRepeatPurchaseRate(memberId);
        model.addAttribute("repeatPurchaseRate", repeatPurchaseRate);
        model.addAttribute("repeatPurchaseRateText", String.format(Locale.KOREA, "%.1f%%", repeatPurchaseRate));

        return "dashboard";
    }
}
