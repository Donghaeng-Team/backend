package com.bytogether.marketservice.dto.request;

import com.bytogether.marketservice.constant.MarketStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 공동 구매 마켓 리스트 요청 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-01
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarketListRequest {

    // 행정구역 코드
    @NotBlank(message = "divisionId must not be blank")
    private String divisionId;

    // 조회 깊이
    // 0 : 해당 행정구역 에서만
    // 1 : 해당 행정구역 + 자식 행정구역 1단계
    // 2 : 해당 행정구역 + 자식 행정구역 2단계
    // 3 : 해당 행정구역 + 자식 행정구역 3단계 (최대)
    // 최대 3단계 까지 허용
    @NotNull(message = "depth must not be null")
    @Min(value = 0, message = "depth must be at least 0")
    @Max(value = 3, message = "depth must be at most 3")
    private Integer depth;

    // 마켓 상태 (RECRUITING, ENDED, CANCELLED) - 삭제는 포함하지 않음
    // 기본값 RECRUITING
    // 허용 가능한 값: RECRUITING, ENDED, CANCELLED, 또는 빈 값
    //    RECRUITING, // 모집 중
    //    ENDED, // 모집 완료
    //    CANCELLED, // 모집 취소
    @Pattern(
            regexp = "^(RECRUITING|ENDED|CANCELLED)?$",
            message = "status must be one of RECRUITING, ENDED, CANCELLED or empty"
    )
    private MarketStatus status = MarketStatus.RECRUITING;

    @Min(value = 0, message = "pageSize must be at least 0")
    private Integer pageNum = 0;

    private String categoryId;

    private String keyword;

}
