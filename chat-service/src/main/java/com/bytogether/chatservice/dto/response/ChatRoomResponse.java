package com.bytogether.chatservice.dto.response;

import com.bytogether.chatservice.entity.ChatRoomStatus;
import com.bytogether.chatservice.entity.ParticipantStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 채팅방 정보를 조회하는 데에 사용하는 dto
 *
 * 1.01
 * 사용자의 채팅방 정렬 및 부가적인 각종 표시용 데이터 추가
 *
 * 1.03
 * currentParticipants -> currentBuyers, maxParticipants -> maxBuyers 명칭 변경
 * currentBuyers 타입 Long -> Integer 변경
 *
 * 1.04
 * currentParticipants 추가(채팅방 전체인원 표시용)
 *
 * @author jhj010311@gmail.com
 * @version 1.04
 * @since 2025-10-17
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
    private Integer maxBuyers;
    private Integer currentBuyers;
    private Integer currentParticipants;
    private ChatRoomStatus status;
    private ParticipantStatus participantStatus;
    private boolean isBuyer;
    private boolean isCreator;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastMessageAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime listOrderTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recruitmentClosedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}