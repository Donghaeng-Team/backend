package com.bytogether.marketservice.client.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ChatRoomCreateRequest {
    private Long marketId;
    private Long creatorUserId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    private Integer minBuyers; // Market 엔티티의 recruitMax
    private Integer maxBuyers; // Market 엔티티의 recruitMin
    private String thumbnailUrl; // 섬네일에 해당하는 s3버킷 이미지 파일 url
    private String title;
}
