package com.semi.controller;

import com.semi.domain.cart.CartItem;
import com.semi.domain.cart.CartItemService;
import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordService;
import com.semi.security.MemberDetails;
import java.util.List;
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

    @PostMapping
    public String dashboard(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam String memberId,
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

        List<CartItem> recentCartItems = cartItemService.getRecentCartItems(memberDetails.getMember().getId());
        model.addAttribute("dashboardMessage", "대시보드");
        model.addAttribute("member", memberDetails.getMember());
        model.addAttribute("recentCartItems", recentCartItems);

        return "dashboard";
    }
}
