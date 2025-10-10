package com.bytogether.marketservice.client;

import com.bytogether.marketservice.client.dto.response.DivisionResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
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
    // 좌표 기반 행정구역 조회
    @GetMapping("/api/v1/division/public/by-coord")
    Optional<DivisionResponseDto> getDivisionByCoord(@RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude);

    // 행정구역 코드 기반 행정구역 조회
    @GetMapping("/internal/v1/division/by-code")
    Optional<DivisionResponseDto> getDivisionByCode(@RequestParam("emdCode") String emdCode);

    // 행정구역 코드 기반 인접동 검색 (읍면동 코드로)
    @GetMapping("/internal/v1/division/near/by-code")
    List<DivisionResponseDto> getNearDivisionsByCode(@RequestParam("depth") Integer depth, @RequestParam("emdCode") String emdCode);
}
