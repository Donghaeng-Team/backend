package com.bytogether.marketservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 공동 구매 마켓 연장 요청 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-30
 */

@Getter
@Setter
public class ExtendMarketRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    @Future
    private LocalDateTime endTime; // 모집 마감 시간

}
