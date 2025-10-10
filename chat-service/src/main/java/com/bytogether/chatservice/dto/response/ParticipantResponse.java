package com.bytogether.chatservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방의 참가인원 정보를 전달하는 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-10
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantResponse {
    private Long userId;
    private String nickname;
    private String profileImage;      // User 서비스에서 조회
    private Boolean isBuyer;          // 구매 의사 확정 여부
    private Boolean isCreator;        // 채팅방 개설자 여부

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime joinedAt;
}