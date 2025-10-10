package com.bytogether.marketservice.dto.request;

import com.bytogether.marketservice.constant.MarketSort;
import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.dto.validation.annotation.MarketSortSubset;
import com.bytogether.marketservice.dto.validation.annotation.MarketStatusSubset;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 공동 구매 마켓 리스트 요청 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-01
 */

// 필드 요약
// - divisionId (String, NotBlank, Pattern: 8자리 숫자) : 행정구역 코드
// - depth (Integer, NotNull, Min: 0, Max: 3) : 조회 깊이
// - status (MarketStatus, MarketStatusSubset) : 마켓 상태
// - categoryId (String) : 카테고리 ID (startsWith 조건)
// - keyword (String) : 검색 키워드 (마켓글 제목, 내용)
// - sort (MarketSort, MarketSortSubset) : 정렬 기준

@Getter
@Setter
public class MarketListRequest extends DefaultPageRequest {

    // 행정구역 코드
    @NotBlank(message = "divisionId must not be blank")
    @Pattern(regexp = "^\\d{8}$")
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
    @MarketStatusSubset(anyOf = {MarketStatus.RECRUITING, MarketStatus.ENDED, MarketStatus.CANCELLED, MarketStatus.REMOVED}, message = "status must be one of {RECRUITING, ENDED, CANCELLED}")
    private MarketStatus status = MarketStatus.RECRUITING;


    // 카테고리 ID (startsWith 조건)
    private String categoryId;

    // 검색 키워드 (마켓글 제목, 내용)
    private String keyword;

    // 정렬 기준 (LATEST, ENDING_SOON, CHEAPEST, MOST_VIEWED)
    @MarketSortSubset(anyOf = {MarketSort.LATEST, MarketSort.ENDING_SOON, MarketSort.CHEAPEST, MarketSort.MOST_VIEWED}, message = "sort must be one of {LATEST, ENDING_SOON, CHEAPEST, MOST_VIEWED}")
    private MarketSort sort = MarketSort.LATEST;

    // Setter에서 빈 문자열 처리
    public void setStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            this.status = null;  // 빈 값은 null로 변환
        } else {
            this.status = MarketStatus.valueOf(statusStr);
        }
    }

    public void setSort(String sortStr) {
        if (sortStr == null || sortStr.isBlank()) {
            this.sort = null;  // 빈 값은 null로 변환
        } else {
            this.sort = MarketSort.valueOf(sortStr);
        }
    }

}
