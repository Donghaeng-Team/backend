package com.bytogether.chatservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방이 마감처리 되었을 때 반환되는 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-10
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentCloseResponse {
    private Long roomId;
    private LocalDateTime closedAt;
    private Integer finalBuyerCount;
    private Integer kickedCount;
}