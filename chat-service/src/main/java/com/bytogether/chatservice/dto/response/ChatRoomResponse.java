package com.bytogether.chatservice.dto.response;

import com.bytogether.chatservice.entity.ChatRoomStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채팅방 정보를 조회하는 데에 사용하는 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.00
 * @since 2025-10-09
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponse {
    private Long id;
    private Long creatorUserId;
    private Long marketId;
    private String title;
    private String thumbnailUrl;
    private Integer maxParticipants;
    private Long currentParticipants;
    private ChatRoomStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recruitmentClosedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}