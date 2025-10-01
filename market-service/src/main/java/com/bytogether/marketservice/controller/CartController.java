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
@RequestMapping("/api/v1/market/private/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartFacadeService cartFacadeService;

//    1.찜하기 추가 addCart - private
//    2.찜하기 삭제 deleteCart - private
//    3.내가 찜한 목록 보기 getMyCarts - private

    // 1. 찜하기 추가 addCart - private
    @PostMapping("/{marketId}")
    public void addCart(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @PathVariable Long marketId) {
        cartFacadeService.addCart(requestUserID, marketId);
    }

    // 2. 찜하기 삭제 deleteCart - private
    @DeleteMapping("/{cartId}")
    public void deleteCart(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @PathVariable Long cartId) {
        cartFacadeService.deleteCart(requestUserID, cartId);

    }

    // 3. 내가 찜한 목록 보기 getMyCarts - private
    @GetMapping("/my")
    public void getMyCarts(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID) {
        cartFacadeService.getMyCarts(requestUserID);
    }


}
