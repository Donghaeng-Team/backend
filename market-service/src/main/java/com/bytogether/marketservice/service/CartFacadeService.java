package com.bytogether.marketservice.service;


import com.bytogether.marketservice.entity.Cart;
import com.bytogether.marketservice.entity.Market;
import com.bytogether.marketservice.service.sub.CartService;
import com.bytogether.marketservice.service.sub.MarketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@Transactional
public class CartFacadeService {
    private final CartService cartService;
    private final MarketService marketService;

    // 찜하기 추가
    public void addCart(Long marketId) {
        // 1. 로그인 사용자 확인 (추후 구현)
        Long userId = 1L; // TODO: 실제 로그인 사용자 ID로 대체

        // 2. 찜하기 추가
        cartService.addCart(userId, marketId);

        // 3. 응답 반환 (추후 구현)
    }

    public void deleteCart(Long cartId) {
        // 1. 로그인 사용자 확인 (추후 구현)
        Long userId = 1L; // TODO: 실제 로그인 사용자 ID로 대체

        // 2. 찜하기 삭제
        cartService.deleteCart(userId, cartId);

        // 3. 응답 반환 (추후 구현)
    }

    public void getMyCarts() {
        // 1. 로그인 사용자 확인 (추후 구현)
        Long userId = 1L; // TODO: 실제 로그인 사용자 ID로 대체
        // 2. 찜하기 목록 조회
        List<Cart> myCarts = cartService.getMyCarts(userId);

        // 3. 마켓 정보 조회
        List<Market> markets =marketService.getMarketsByIds(
                myCarts.stream().map(Cart::getMarketId).toList()
        );

        // 4. 결과 반환 (추후 구현)
    }
}
