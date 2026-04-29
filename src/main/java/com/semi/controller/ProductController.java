package com.semi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.semi.domain.product.Product;
import com.semi.domain.product.ProductService;
import com.semi.security.MemberDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping({"/product", "/"})
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    
    @GetMapping("/")
    public String getAllProduct(Model model) {
        List<Product> products = productService.getAllProduct();
        model.addAttribute("products", products);
        return "index";
    }

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
        List<Product> products = productService.searchProductsByName(trimmedQuery);
        model.addAttribute("query", trimmedQuery);
        model.addAttribute("products", products);
        return "dashboard_search_result";
    }

    
}
