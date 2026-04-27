package com.semi.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.semi.domain.keyword.TrendKeyword;
import com.semi.domain.keyword.TrendKeywordService;
import com.semi.domain.product.Product;
import com.semi.domain.product.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping({"/product", "/"})
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final TrendKeywordService trendKeywordService;



    @GetMapping("/member")    
    public String getTrendKeywords(Model model){
        List<TrendKeyword> keywords = trendKeywordService.getKeywords();
        // ID 필드를 기준으로 비교하는 규칙(Comparator) 생성
        Comparator<TrendKeyword> idComparator = Comparator.comparing(TrendKeyword::getId);
        model.addAttribute("idComparator", idComparator);
        model.addAttribute("keywords", keywords);
        return "dashboard";
    }
    @GetMapping("/")
    public String getAllProduct(Model model) {
        List<Product> products = productService.getAllProduct();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/view")
    public String viewProductDetail(@RequestParam("id") String id, Model model) {
        Product product = productService.getProductDetail(Long.parseLong(id));
        System.out.println(product.toString());
        model.addAttribute("product", product);
        return "product";
    }
    
}
