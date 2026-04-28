package com.semi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @GetMapping
    public String adminPage(Model model) {
        // 항상 대시보드 페이지로 리다이렉트
        return "admin-dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/orders")
    public String orders() {
        return "admin-orders";
    }

    @GetMapping("/products")
    public String products() {
        return "admin-products";
    }

    @GetMapping("/members")
    public String members() {
        return "admin-members";
    }
}
