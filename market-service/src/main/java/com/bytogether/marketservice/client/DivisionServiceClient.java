package com.bytogether.marketservice.client;

import com.bytogether.marketservice.client.dto.response.DivisionResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * Division Service와 통신하기 위한 Feign Client
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-31
 */

@FeignClient(name = "division-service")
public interface DivisionServiceClient {
    @GetMapping("/api/v1/division/public/by-coord")
    Optional<DivisionResponseDto> getDivisionByCoord(@RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude);

    @GetMapping("/api/v1/division/private/near/by-code")
    Optional<DivisionResponseDto> getDivisionByCoord(@RequestParam("emyCode") String latitude, @RequestParam("longitude") Double longitude);
}
