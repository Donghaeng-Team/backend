package com.bytogether.marketservice.controller;


import com.bytogether.marketservice.dto.ApiResponse;
import com.bytogether.marketservice.dto.request.CartListRequest;
import com.bytogether.marketservice.dto.response.CartResponse;
import com.bytogether.marketservice.dto.response.MarketListResponse;
import com.bytogether.marketservice.service.CartFacadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

//    1.찜하기 추가 addCart - private (완료)
//    2.찜하기 삭제 deleteCart - private (완료)
//    3.내가 찜한 목록 보기 getMyCarts - private (완료)

    // 1. 찜하기 추가 addCart - private
    @PostMapping("/{marketId}")
    public ResponseEntity<ApiResponse<?>> addCart(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @PathVariable Long marketId) {
        CartResponse cartResponse = cartFacadeService.addCart(requestUserID, marketId);
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }

    // 2. 찜하기 삭제 deleteCart - private
    @DeleteMapping("/{cartId}")
    public ResponseEntity<ApiResponse<?>> deleteCart(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @PathVariable Long cartId) {
        cartFacadeService.deleteCart(requestUserID, cartId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 3. 내가 찜한 목록 보기 getMyCarts - private
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<MarketListResponse>> getMyCarts(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @Valid CartListRequest cartListRequest) {
        MarketListResponse myCarts = cartFacadeService.getMyCarts(requestUserID, cartListRequest);
        return ResponseEntity.ok(ApiResponse.success(myCarts));
    }


}
