package com.bytogether.marketservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 공동 구매 마켓 수정 요청 DTO
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
public class PutMarketRequest {
//    모집 중 일 때만 변경가능
//
//    이미지 변경가능
//    제목 변경가능
//    카테고리 변경가능
//    마감 시간 변경가능
//    내용 변경가능

    private List<MultipartFile> images; // 이미지 파일

    @NotBlank
    private String title; // 제목

    @NotBlank
    private String categoryId; // 카테고리 ID

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    @Future
    private LocalDateTime endTime; // 모집 마감 시간

    @NotBlank
    private String content; // 내용

}
