package com.semi.controller;

import com.semi.domain.product.Product;
import com.semi.domain.product.ProductRepository;
import com.semi.domain.product.dto.UpdateAutoOrderRequest;
import com.semi.security.MemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductRepository productRepository;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}/auto-order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAutoOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAutoOrderRequest request,
            @AuthenticationPrincipal MemberDetails changedBy
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update auto order status
        product.updateAutoOrder(request.autoOrder());

        // Save and return updated product
        Product updatedProduct = productRepository.save(product);

        return ResponseEntity.ok(java.util.Map.of(
                "productId", updatedProduct.getId(),
                "productName", updatedProduct.getName(),
                "autoOrder", updatedProduct.getAutoOrder(),
                "message", "Auto order status updated successfully"
        ));
    }
}
