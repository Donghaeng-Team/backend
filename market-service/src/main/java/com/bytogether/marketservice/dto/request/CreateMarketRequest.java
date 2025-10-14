package com.bytogether.marketservice.dto.request;

import com.bytogether.marketservice.dto.validation.annotation.RecruitonMatches;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 공동 구매 마켓 생성 요청 DTO
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Getter
@Setter
@RecruitonMatches
public class CreateMarketRequest {

    private List<MultipartFile> images; // 이미지 파일

    @NotBlank
    private String title; // 제목

    @NotBlank
    private String categoryId; // 카테고리 ID

    @NotNull
    @Min(0)
    private Long price; // 가격

    @NotNull
    @Min(2)
    private Integer recruitMin; // 최소 모집 인원

    @NotNull
    @Min(2)
    private Integer recruitMax; // 최대 모집 인원

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    @Future
    private LocalDateTime endTime; // 모집 마감 시간

    @NotBlank
    private String content; // 내용

    @NotNull
    @Min(33)
    @Max(39)
    private BigDecimal latitude; // 위도

    @NotNull
    @Min(124)
    @Max(132)
    private BigDecimal longitude; // 경도

    @NotBlank
    private String locationText; // 위치 설명

}
