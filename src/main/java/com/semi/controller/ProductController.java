package com.semi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.semi.domain.product.Product;
import com.semi.domain.product.ProductService;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping({"/product", "/"})
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping({"/list", "/"})
    public String getAllProduct(Model model) {
        List<Product> products = productService.getAllProduct();
        model.addAttribute("products", products);
        return "index";
    }
}
