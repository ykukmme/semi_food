package com.semi.domain.product;

<<<<<<< HEAD
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
=======
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

>>>>>>> 1ade278 (fix: Thymeleaf 3.1 security error on index.html)
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

<<<<<<< HEAD
    /**
     * 상품 전부 가져오기
     * 
     */
    @Transactional
    public List<Product> getProductList() {
=======
    @Transactional
    public List<Product> getAllProduct(){
>>>>>>> 1ade278 (fix: Thymeleaf 3.1 security error on index.html)
        return productRepository.findAll();
    }
}
