package com.bytogether.marketservice.dto.response;

import com.bytogether.marketservice.entity.Market;
import lombok.*;

/**
 * 공동 구매 마켓 수정 응답 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-30
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PutMarketResponse {
    private Long marketId;

    public static PutMarketResponse fromEntity(Market market) {
        return PutMarketResponse.builder()
                .marketId(market.getId())
                .build();
    }
}
