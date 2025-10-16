package com.bytogether.marketservice.dto.response;

import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.entity.Image;
import com.bytogether.marketservice.entity.Market;
import lombok.*;

/**
 * 공동 구매 마켓 심플 응답 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-01
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MarketSimpleResponse {
    // 상세 조회에 필요
    private Long marketId;

    // 화면 표시에 필요
    private String title;
    private String categoryId;
    private Long price;
    private String emdName;
    private Integer recruitNow; // 현재 모집 인원

    private Integer recruitMax;
    private MarketStatus status;
    private String thumbnailImageUrl; // 썸네일 이미지 URL
    private String nickname; // 작성자 닉네임
    private String userProfileImageUrl; // 작성자 프로필 이미지 URL


    public static MarketSimpleResponse fromEntity(Market market, Integer recruitNow, String authorNickname, String authorProfileImageUrl) {

        String thumbnailImageUrl = market.getImages().stream()
                .filter(image -> image.getSortOrder() == 0)
                .map(Image::getFilePath)
                .findFirst()
                .orElse(null);

        return MarketSimpleResponse.builder()
                .marketId(market.getId())

                .title(market.getTitle())
                .categoryId(market.getCategoryId())
                .price(market.getPrice())
                .emdName(market.getEmdName())
                .recruitNow(recruitNow)

                .recruitMax(market.getRecruitMax())
                .status(market.getStatus())
                .thumbnailImageUrl(thumbnailImageUrl)
                .nickname(authorNickname)
                .userProfileImageUrl(authorProfileImageUrl)
                .build();
    }


}
