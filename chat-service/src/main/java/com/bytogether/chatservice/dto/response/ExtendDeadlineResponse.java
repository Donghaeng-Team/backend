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
 * @version 1.01
 * @since 2025-10-17
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtendDeadlineResponse {
    private Long roomId;
    private LocalDateTime newDeadline;
    private Integer extendedHours;
}