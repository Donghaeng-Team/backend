package com.bytogether.chatservice.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 채팅방 목록조회를 요청하는 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-15
 */

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomListRequest {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime cursor;

    private Long participantId;

    @Min(1)
    private int size = 20;  // 기본값
}