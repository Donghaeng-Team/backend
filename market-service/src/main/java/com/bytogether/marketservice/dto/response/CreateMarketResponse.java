package com.bytogether.marketservice.dto.response;

import com.bytogether.marketservice.entity.Market;
import lombok.*;

/**
 * 공동 구매 마켓 생성 응답 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreateMarketResponse {
    private Long marketId;

    public static CreateMarketResponse fromEntity(Market market) {
        return CreateMarketResponse.builder()
                .marketId(market.getId())
                .build();
    }
}
