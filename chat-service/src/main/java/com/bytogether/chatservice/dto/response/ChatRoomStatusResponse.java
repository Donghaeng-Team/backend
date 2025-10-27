package com.bytogether.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 채팅방의 공동구매 참가인원에 변동이 있을 경우
 * 해당 정보를 전달하는 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-10
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomStatusResponse {
    private Long chatRoomId;
    private Integer currentParticipants;  // 현재 참가자 수
    private Integer buyerCount;           // 구매 의사 확정 수
    private Integer maxParticipants;      // 구매참가 최대 인원
    private String status;                // 채팅방 상태
}