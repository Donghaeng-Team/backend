package com.bytogether.marketservice.dto.response;


import com.bytogether.marketservice.client.dto.response.UserInternalResponse;
import com.bytogether.marketservice.entity.Market;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이지 응답 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 * <p>
 * 미완성 - recruitNow 필드 제대로 처리하기 - 2025-10-10
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MarketListResponse extends DefaultPageResponse {
    private List<MarketSimpleResponse> markets;

    public static MarketListResponse fromEntities(Page<Market> markets, List<UserInternalResponse> users) {
        List<MarketSimpleResponse> marketResponses = markets.stream()
                .map(market -> {
                    UserInternalResponse user = users.stream()
                            .filter(u -> u.getUserId().equals(market.getAuthorId()))
                            .findFirst()
                            .orElse(null);
                    return MarketSimpleResponse.fromEntity(market, 0, user.getNickName(), user.getImageUrl());
                })
                .toList();

        MarketListResponse marketListResponse = new MarketListResponse(marketResponses);
        marketListResponse.setTotalElements(markets.getTotalElements());
        marketListResponse.setTotalPages(markets.getTotalPages());
        marketListResponse.setCurrentPage(markets.getNumber());
        marketListResponse.setPageSize(markets.getSize());

        return marketListResponse;
    }
}
