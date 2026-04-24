package com.semi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.semi.domain.product.Product;
import com.semi.domain.product.ProductService;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/product/**")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
    public String getProductList(Model model) {
        List<Product> productList = productService.getProductList();
        // productList.toString();
        model.addAttribute("products", productList);
        return "index";
    }
    

}
