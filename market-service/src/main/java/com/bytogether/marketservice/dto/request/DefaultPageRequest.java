package com.bytogether.marketservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * 페이징 요청 DTO
 *
 * @author insu9058
 * @version 1.0
 * @since 2025-10-10
 */

@Getter
@Setter
public class DefaultPageRequest {
    // 요청 페이지 번호 (0부터 시작)
    @Min(value = 0, message = "pageSize must be at least 0")
    private Integer pageNum = 0;

    // 페이지 당 조회 개수
    @Min(value = 10, message = "pageSize must be at least 10")
    @Max(value = 100, message = "pageSize must be at most 100")
    private Integer pageSize = 20;
}
