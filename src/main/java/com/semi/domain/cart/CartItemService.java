package com.semi.domain.cart;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.semi.domain.member.Member;
import com.semi.domain.member.MemberRepository;
import com.semi.domain.product.Product;
import com.semi.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public CartItem addToCartItem(Long memberId, Long productId, int quantity){
        Member member = memberRepository.findById(memberId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        CartItem cartItem = cartItemRepository.findByMemberIdAndProductId(memberId, productId)
        .map(existing ->{
            existing.updateQuantity(existing.getQuantity() + quantity);
            return existing;
        }).orElseGet(()-> CartItem.builder()
            .member(member)
            .product(product)
            .quantity(quantity)
            .build());
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void deleteCartItem(Long memberId, Long productId) {
        cartItemRepository.deleteByMemberIdAndProductId(memberId, productId);
    }

    @Transactional
    public CartItem updateCartItemQuantity(Long memberId, Long productId, int quantity) {
        return cartItemRepository.findByMemberIdAndProductId(memberId, productId)
                .map(cartItem -> {
                    cartItem.updateQuantity(quantity);
                    return cartItem;
                })
                .orElseGet(() -> {
                    Member member = memberRepository.findById(memberId).orElseThrow();
                    Product product = productRepository.findById(productId).orElseThrow();
                    CartItem cartItem = CartItem.builder()
                            .member(member)
                            .product(product)
                            .quantity(quantity)
                            .build();
                    return cartItemRepository.save(cartItem);
                });
    }
}
