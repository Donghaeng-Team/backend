package com.bytogether.marketservice.constant;

/**
 * 공동 구매 마켓 상태 Enum
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

public enum MarketStatus {
    RECRUITING, // 모집 중
    ENDED, // 모집 완료
    CANCELLED, // 모집 취소
    REMOVED // 마켓 삭제 사실상 사용 안하는중 251023 15:41
}
