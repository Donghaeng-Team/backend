package com.bytogether.marketservice.dto.response;

import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.entity.Image;
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
    private Long marketId;
    private String categoryId;
    private LocalDateTime endTime;
    private Long price;
    private Integer recruitMin;
    private Integer recruitMax;
    private MarketStatus status;
    private String title;
    private String content;
    private Long authorId;
    private String authorNickname; // api 요청으로 가져오기
    private String authorProfileImageUrl; // api 요청으로 가져오기
    private String locationText;
    private String divisionId;
    private String emdName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer views;

    private List<Image> images;
}
