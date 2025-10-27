package com.bytogether.chatservice.client;

import com.bytogether.chatservice.client.dto.UserInternalResponse;
import com.bytogether.chatservice.client.dto.UsersInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Market Service와 통신하기 위한 Feign Client
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-23
 *
 */

@FeignClient(name = "market-service")
public interface MarketServiceClient {

    @GetMapping("/internal/v1/market/status/cancel/{marketId}")
    void cancelMarketPost(@PathVariable Long marketId, @RequestParam Long requestUserId);

    @GetMapping("/internal/v1/market/status/complete/{marketId}")
    void completeMarketPost(@PathVariable Long marketId, @RequestParam Long requestUserId);
}
