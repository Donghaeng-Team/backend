package com.bytogether.marketservice.controller;


import com.bytogether.marketservice.dto.request.CreateMarketRequest;
import com.bytogether.marketservice.dto.response.CreateMarketResponse;
import com.bytogether.marketservice.service.MarketFacadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 공동 구매 마켓 관련 API 컨트롤러
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketFacadeService marketFacadeService;

    // 마켓글 작성
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CreateMarketResponse> createMarketPost(@Valid @ModelAttribute CreateMarketRequest createMarketRequest) {

        CreateMarketResponse marketPost = marketFacadeService.createMarketPost(createMarketRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(marketPost);
    }

    // 마켓글 수정
    @PutMapping("/{marketId}")
    public String updateMarketPost(@PathVariable Long marketId) {
        return "Market post " + marketId + " updated";
    }

    // 마켓글 삭제(취소)
    // TODO: 실제로는 삭제가 아닌 취소
    @DeleteMapping("/{marketId}")
    public String deleteMarketPost(@PathVariable Long marketId) {
        marketFacadeService.deleteMarketPost(marketId);
        return "Market post " + marketId + " deleted";
    }

    // 마켓글 연장
    @PatchMapping("/extend/{marketId}")
    public String extendMarketPost(@PathVariable Long marketId) {
        return "Market post " + marketId + " extended";
    }

    // 마켓글 목록 조회 (필터링, 페이징)
    @GetMapping
    public String getMarketPosts() {
        return "List of market posts";
    }

    // 마켓글 상세 조회
    @GetMapping("/{marketId}")
    public String getMarketPostDetail(@PathVariable Long marketId) {
        return "Details of market post " + marketId;
    }

    // 마켓글 상태 변경 (모집중, 거래완료, 취소 등)
    @PatchMapping("/status/{marketId}")
    public String changeMarketPostStatus(@PathVariable Long marketId, @RequestParam String status) {
        return "Market post " + marketId + " status changed to " + status;
    }

    // 자신의 마켓글 조회
    @GetMapping("/my")
    public String getMyMarketPosts() {
        marketFacadeService.getMyMarketPosts();
        return "List of my market posts";
    }


}
