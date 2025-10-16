package com.bytogether.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KickNotificationResponse {
    private Long roomId;
    private String reason;
    private LocalDateTime kickedAt;
}