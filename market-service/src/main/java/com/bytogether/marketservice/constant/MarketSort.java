package com.bytogether.marketservice.constant;

/**
 * 공동 구매 마켓 정렬 기준 Enum
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

public enum MarketSort {
    // 최신순
    // 마감임박순
    // 저렴한순
    // 조회수순
    // 가까운순 (추후 구현 고려) - 위치 기반 (GPS 허용시에만 적용)

    LATEST,
    ENDING_SOON,
    CHEAPEST,
    MOST_VIEWED,
//    NEARBY

}
