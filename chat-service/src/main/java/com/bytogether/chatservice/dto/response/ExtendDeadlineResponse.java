package com.bytogether.chatservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방의 마감기한을 연장할 때 반환되는 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-10
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtendDeadlineResponse {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime originalEndTime;  // 기존 마감 시간

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime newEndTime;       // 연장된 마감 시간

    private String message;
}