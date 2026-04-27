package com.semi.domain.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    @Transactional
    public Product getProductDetail(Long id){
        return productRepository.findProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. id=" + id));
    }

}
