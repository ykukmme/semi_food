package com.semi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.semi.domain.cart.CartItem;
import com.semi.domain.cart.CartItemService;
import com.semi.domain.cart.dto.AddCartItemRequest;
import com.semi.domain.product.Product;
import com.semi.domain.product.ProductService;
import com.semi.security.MemberDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemService cartItemService;
    private final ProductService productService;

    @GetMapping("/view")
    public String viewCart(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam(required = false) Long memberId,
            Model model
    ) {
        if (memberDetails == null) {
            return "redirect:/login.html";
        }

        Long resolvedMemberId = memberDetails.getMember().getId();
        List<CartItem> cartItems = cartItemService.getCartItems(resolvedMemberId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartRows", toCartRows(cartItems));
        model.addAttribute("cartLoadedFromServer", true);
        return "cart";
    }

    @PostMapping("/view")
    public String addCartItemAndViewCart(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam(required = false) Long memberId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Model model
    ) {
        Long resolvedMemberId = memberDetails != null ? memberDetails.getMember().getId() : memberId;
        if (resolvedMemberId == null) {
            return "redirect:/login.html";
        }

        CartItem cartItem = cartItemService.addToCartItem(resolvedMemberId, productId, quantity);
        Product product = productService.getProductDetail(cartItem.getProduct().getId());

        model.addAttribute("cartItem", cartItem);
        model.addAttribute("product", product);
        List<CartItem> cartItems = cartItemService.getCartItems(resolvedMemberId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartRows", toCartRows(cartItems));
        model.addAttribute("cartLoadedFromServer", true);
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

    @DeleteMapping("/items/{productId}")
    @ResponseBody
    public ResponseEntity<Void> deleteCartItem(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable Long productId
    ) {
        cartItemService.deleteCartItem(memberDetails.getMember().getId(), productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/items")
    @ResponseBody
    public ResponseEntity<?> updateCartItemQuantity(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        CartItem cartItem = cartItemService.updateCartItemQuantity(
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

    private List<Map<String, Object>> toCartRows(List<CartItem> cartItems) {
        if (cartItems == null) {
            return List.of();
        }

        return cartItems.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    return Map.<String, Object>of(
                            "id", cartItem.getId(),
                            "cartItemId", cartItem.getId(),
                            "productId", product.getId(),
                            "name", product.getName(),
                            "collection", "Heritage Namhae",
                            "price", product.getPrice() == null ? 0 : product.getPrice(),
                            "quantity", cartItem.getQuantity(),
                            "image", product.getImageUrl() == null ? "" : product.getImageUrl(),
                            "minQty", 1,
                            "checked", true
                    );
                })
                .toList();
    }
}
