package com.semi.domain.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 전부 가져오기
     * 
     */
    @Transactional
    public List<Product> getProductList() {
        return productRepository.findAll();
    }
}
