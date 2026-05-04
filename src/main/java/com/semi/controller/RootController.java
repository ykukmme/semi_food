package com.semi.controller;

import com.semi.domain.product.Product;
import com.semi.domain.product.ProductService;
import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RootController {

    private final ProductService productService;
    private final TrendKeywordService trendKeywordService;

    @GetMapping("/")
    public String index(Model model) {
        List<Product> products = productService.getAllProduct();
        LocalDate keywordCollectedDate = LocalDate.of(2026, 4, 27);
        List<TrendKeyword> keywords = trendKeywordService.getKeywordsCollectedOnOrderById(keywordCollectedDate);
        keywords.stream()
                .filter(keyword -> keyword.getCollectedAt() != null)
                .max((left, right) -> left.getCollectedAt().compareTo(right.getCollectedAt()))
                .ifPresent(keyword -> model.addAttribute("keywordCollectedAt", keyword.getCollectedAt()));
        model.addAttribute("products", products);
        model.addAttribute("keywords", keywords);
        return "index";
    }

    @GetMapping("/index.html")
    public String indexHtml(Model model) {
        return index(model);
    }
}
