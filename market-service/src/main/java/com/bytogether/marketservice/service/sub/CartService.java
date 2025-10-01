package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.constant.CartStatus;
import com.bytogether.marketservice.entity.Cart;
import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 공동 구매 찜하기 관련 서비스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    // 찜하기 추가
    public void addCart(Long userId, Long marketId) {
        Cart cart = Cart.createCart(userId, marketId);
        cartRepository.save(cart);
    }

    // 찜하기 삭제
    public void deleteCart(Long userId, Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new MarketException("찜하기 항목이 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        if (!cart.getUserId().equals(userId)) {
            throw new MarketException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cart.setStatus(CartStatus.REMOVED);
        cartRepository.save(cart);
    }

    // 내 찜하기 목록 조회
    public List<Cart> getMyCarts(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ADDED);
    }

    // marketId로 찜하기 횟수 조회
    public long countCartsByMarketId(Long marketId) {
        return cartRepository.findByMarketIdAndStatus(marketId, CartStatus.ADDED).size();
    }

}
