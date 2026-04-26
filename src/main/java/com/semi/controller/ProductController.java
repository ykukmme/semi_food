package com.semi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
=======
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
>>>>>>> 1ade278 (fix: Thymeleaf 3.1 security error on index.html)

import com.semi.domain.product.Product;
import com.semi.domain.product.ProductService;

import lombok.RequiredArgsConstructor;


@Controller
<<<<<<< HEAD
@RequestMapping("/product/**")
=======
@RequestMapping("/product")
>>>>>>> 1ade278 (fix: Thymeleaf 3.1 security error on index.html)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/list")
<<<<<<< HEAD
    public String getProductList(Model model) {
        List<Product> productList = productService.getProductList();
        // productList.toString();
        model.addAttribute("products", productList);
        return "index";
    }
    

=======
    public String getAllProduct(Model model) {
        List<Product> products = productService.getAllProduct();
        model.addAttribute("products", products);
        return "index";
    }
>>>>>>> 1ade278 (fix: Thymeleaf 3.1 security error on index.html)
}
