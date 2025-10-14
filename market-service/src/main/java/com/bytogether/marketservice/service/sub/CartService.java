package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.constant.CartStatus;
import com.bytogether.marketservice.entity.Cart;
import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public Cart addCart(Long userId, Long marketId) {
        // 이미 찜한 마켓인지 확인
        Cart cart = null;
        Optional<Cart> byUserIdAndMarketIdAndStatus = cartRepository.findByUserIdAndMarketIdAndStatus(userId, marketId, CartStatus.ADDED);

        if (byUserIdAndMarketIdAndStatus.isEmpty()) {
            // 찜하기 추가
            Cart savedCart = Cart.createCart(userId, marketId);
            cart = cartRepository.save(savedCart);
        } else {
            cart = byUserIdAndMarketIdAndStatus.get();
        }

        return cart;
    }

    // 찜하기 삭제
    public void deleteCart(Long userId, Long marketId) {
        // 존재하는 찜하기인지 확인
        Optional<Cart> byUserIdAndMarketIdAndStatus = cartRepository.findByUserIdAndMarketIdAndStatus(userId, marketId, CartStatus.ADDED);

        if (byUserIdAndMarketIdAndStatus.isEmpty()) {
            // 찜하기가 존재하지 않음
            return; // 존재하지 않으면 아무 작업도 수행하지 않음
        }else {
            // 찜하기가 존재함
            Cart existingCart = byUserIdAndMarketIdAndStatus.get();
            existingCart.setStatus(CartStatus.REMOVED);
            cartRepository.save(existingCart);
        }

    }

    // 내 찜하기 목록 조회
    public Page<Cart> getMyCarts(Long userId, PageRequest pageRequest) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ADDED, pageRequest);
    }

    // marketId로 찜하기 횟수 조회 - 사용 안할 것으로 보임 (2025-10-10)
    public long countCartsByMarketId(Long marketId) {
        return cartRepository.findByMarketIdAndStatus(marketId, CartStatus.ADDED).size();
    }

}
