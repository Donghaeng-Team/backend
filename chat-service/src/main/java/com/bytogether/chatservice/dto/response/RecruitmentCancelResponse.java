package com.bytogether.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentCancelResponse {
    private Long roomId;
    private LocalDateTime canceledAt;
}
