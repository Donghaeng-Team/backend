package com.bytogether.marketservice.controller;

import com.bytogether.marketservice.dto.ApiResponse;
import com.bytogether.marketservice.dto.request.DefaultPageRequest;
import com.bytogether.marketservice.dto.request.MarketListRequest;
import com.bytogether.marketservice.dto.response.MarketDetailResponse;
import com.bytogether.marketservice.dto.response.MarketListResponse;
import com.bytogether.marketservice.service.MarketFacadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 공동 구매 마켓 관련 public API 컨트롤러
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-30
 */

@RestController
@RequestMapping("/api/v1/market/public")
@RequiredArgsConstructor
public class MarketPublicController {
    private final MarketFacadeService marketFacadeService;
//    1.마켓글 목록 조회 (필터링, 페이징) getMarketPosts - public (완료)
//    2.마켓글 상세 조회 getMarketPostDetail - public (완료)
//    3.특정 유저의 마켓글 검색 getPostsByUserId - public (완료)

    // 1. 마켓글 목록 조회 (필터링, 페이징) getMarketPosts - public
    @GetMapping
    public ResponseEntity<ApiResponse<MarketListResponse>> getMarketPosts(@Valid MarketListRequest marketListRequest) {
        MarketListResponse response = marketFacadeService.getMarketPosts(null, marketListRequest);

        
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    // 2. 마켓글 상세 조회 getMarketPostDetail - public
    @GetMapping("/{marketId}")
    public ResponseEntity<ApiResponse<MarketDetailResponse>> getMarketPostDetail(@PathVariable Long marketId) {
        MarketDetailResponse response = marketFacadeService.getMarketPostDetailWithLogin(null, marketId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    // 3. 특정 유저의 마켓글 목록 조회 getPostsByUserId - public
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<MarketListResponse>> getPostsByUserId(@PathVariable("userId") Long targetUserId, @Valid DefaultPageRequest defaultPageRequest) {
        MarketListResponse someonesMarketPosts = marketFacadeService.getSomeonesMarketPosts(targetUserId, defaultPageRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(someonesMarketPosts));
    }
}
