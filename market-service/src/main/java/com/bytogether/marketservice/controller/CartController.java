package com.bytogether.marketservice.controller;


import com.bytogether.marketservice.service.CartFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 공동 구매 찜하기 관련 컨트롤러
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@RestController
@RequestMapping("/api/v1/market/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartFacadeService cartFacadeService;

    // 찜하기 추가
    @PostMapping("/{marketId}")
    public void addCart(@PathVariable Long marketId) {
        cartFacadeService.addCart(marketId);
    }

    // 찜하기 삭제
    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable Long cartId) {
        cartFacadeService.deleteCart(cartId);

    }

    // 내 찜하기 목록 조회
    @GetMapping("/my")
    public void getMyCarts() {
        cartFacadeService.getMyCarts();
    }


}
