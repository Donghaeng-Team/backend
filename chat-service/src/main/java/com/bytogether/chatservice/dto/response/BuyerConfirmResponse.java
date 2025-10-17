package com.bytogether.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공동구매 참가 혹은 취소시 반환되는 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-17
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerConfirmResponse {
    private Long userId;
    private Boolean isBuyer;            // 현재 구매 의사 상태
    private Integer currentBuyers;      // 현재 총 구매자 수
    private LocalDateTime confirmedAt;
}