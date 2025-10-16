package com.bytogether.chatservice.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 채팅 조회를 요청하는 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-16
 */

@Getter
@Setter
@NoArgsConstructor
public class ChatMessagePageRequest {
    private Long cursor;
    private Long roomId;

    @Min(1)
    private int size = 50;  // 기본값
}