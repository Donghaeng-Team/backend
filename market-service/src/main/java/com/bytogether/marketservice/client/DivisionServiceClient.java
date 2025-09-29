package com.bytogether.marketservice.client;

import com.bytogether.marketservice.client.dto.DivisionResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "division-service")
public interface DivisionServiceClient {
    @GetMapping("/api/v1/division/by-coord")
    Optional<DivisionResponseDto> getDivisionByCoord(@RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude);
}
