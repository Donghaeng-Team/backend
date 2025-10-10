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
    private String status;            // RECRUITMENT_CLOSED
    private Integer finalBuyerCount;  // 확정된 구매자 수
    private Integer kickedCount;      // 자동 퇴장된 인원 수

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime closedAt;
}