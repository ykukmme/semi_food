package com.semi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.semi.domain.cart.CartItem;
import com.semi.domain.cart.CartItemService;
import com.semi.domain.cart.dto.AddCartItemRequest;
import com.semi.domain.product.Product;
import com.semi.domain.product.ProductService;
import com.semi.security.MemberDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemService cartItemService;
    private final ProductService productService;

    @GetMapping("/view")
    public String saveCartItem(Long memberId, Long productId, int quantity, Model model){
        CartItem cartItem = cartItemService.addToCartItem(memberId, productId, quantity);
        Product product = productService.getProductDetail(productId);
        model.addAttribute("cartItem", cartItem);
        model.addAttribute("product", product);
        System.out.println(cartItem.toString());
        return "cart";
    }

    @PostMapping("/items")
    @ResponseBody
    public ResponseEntity<?> addCartItem(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        CartItem cartItem = cartItemService.addToCartItem(
                memberDetails.getMember().getId(),
                request.productId(),
                request.quantity()
        );

        return ResponseEntity.ok().body(java.util.Map.of(
                "id", cartItem.getId(),
                "productId", request.productId(),
                "quantity", cartItem.getQuantity()
        ));
    }

}
