package com.bytogether.marketservice.dto.response;

import com.bytogether.marketservice.client.dto.response.ParticipantResponse;
import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.entity.Market;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 공동 구매 마켓 상세 응답 DTO
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
public class MarketDetailResponse {
    private Long marketId; // from Entity
    private String categoryId; // from Entity
    private LocalDateTime endTime; // from Entity
    private Long price; // from Entity
    private Integer recruitMin; // from Entity

    private Integer recruitMax; // from Entity
    private Integer recruitNow; // api 요청으로 가져오기 TODO: 추후 구현 필요 - 2025-10-10
    private MarketStatus status; // from Entity
    private String title; // from Entity
    private String content; // from Entity
    private Long authorId; // from Entity

    private String authorNickname; // api 요청으로 가져오기
    private String authorProfileImageUrl; // api 요청으로 가져오기

    private List<ParticipantResponse> participants; // api 요청으로 가져오기
    private Long chatRoomId; // api 요청으로 가져오기

    private String locationText; // from Entity
    private String divisionId; // from Entity
    private String emdName; // from Entity
    private BigDecimal latitude; // from Entity
    private BigDecimal longitude; // from Entity

    private LocalDateTime createdAt; // from Entity
    private LocalDateTime updatedAt; // from Entity
    private Integer views; // from Entity
    private List<ImageResponse> images; // from Entity

    public static MarketDetailResponse fromEntity(Market market) {

        List<ImageResponse> imageResponses = market.getImages().stream()
                .filter(image -> image.getSortOrder() != 0) // market이 null인 경우 필터링
                .map(ImageResponse::fromEntity)
                .toList();

        return MarketDetailResponse.builder()
                .marketId(market.getId())
                .categoryId(market.getCategoryId())
                .endTime(market.getEndTime())
                .price(market.getPrice())
                .recruitMin(market.getRecruitMin())

                .recruitMax(market.getRecruitMax())
                .status(market.getStatus())
                .title(market.getTitle())
                .content(market.getContent())
                .authorId(market.getAuthorId())

                .locationText(market.getLocationText())
                .divisionId(market.getDivisionId())
                .emdName(market.getEmdName())
                .latitude(market.getLatitude())
                .longitude(market.getLongitude())

                .createdAt(market.getCreatedAt())
                .updatedAt(market.getUpdatedAt())
                .views(market.getViews())
                .images(imageResponses)

                .build();
    }
}
