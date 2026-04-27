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
        // 최적화된 쿼리 사용 - N+1 문제 해결 및 데이터베이스 레벨 정렬
        List<Product> products = productRepository.findAllOptimized();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/list/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> getProductsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 페이징 처리 - 한 번에 50개씩 로드
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<Product> productPage = productRepository.findProductsPaged(pageable);
        return ResponseEntity.ok(productPage.getContent());
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
