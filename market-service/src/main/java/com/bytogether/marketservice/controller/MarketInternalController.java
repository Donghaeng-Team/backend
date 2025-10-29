package com.bytogether.marketservice.controller;

import com.bytogether.marketservice.dto.request.ExtendMarketRequest;
import com.bytogether.marketservice.dto.response.ExtendMarketResponse;
import com.bytogether.marketservice.service.MarketFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/market")
public class MarketInternalController {
    private final MarketFacadeService marketFacadeService;

    // 마켓글 취소 처리
    @GetMapping("/status/cancel/{marketId}")
    public void cancelMarketPost(@PathVariable Long marketId, @RequestParam Long requestUserId) {
        marketFacadeService.cancelMarketPost(requestUserId, marketId);
    }

    // 마켓글 거래 완료 처리
    @GetMapping("/status/complete/{marketId}")
    public void completeMarketPost(@PathVariable Long marketId, @RequestParam Long requestUserId) {
        marketFacadeService.completeMarketPost(requestUserId, marketId);
    }

    // 4. 마켓글 연장 extendMarketPost - private (완료)
    @PostMapping("/extend/{marketId}")
    public void extendMarketPost(@RequestParam Long requestUserId, @PathVariable Long marketId, @RequestBody ExtendMarketRequest extendMarketRequest) {
        ExtendMarketResponse extendMarketResponse = marketFacadeService.extendMarketPost(requestUserId, marketId, extendMarketRequest);
    }

}
