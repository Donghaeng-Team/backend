package com.bytogether.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공동구매 참가 혹은 취소시 반환되는 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-10
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerConfirmResponse {
    private Long userId;
    private Boolean isBuyer;          // 현재 구매 의사 상태
    private Integer currentBuyerCount; // 현재 총 구매자 수
    private Integer maxParticipants;   // 최대 인원
}