package com.bytogether.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅방의 참가인원 목록 정보를 전달하는 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-10
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantListResponse {
    private Integer totalCount;       // 총 참가자 수
    private Integer buyerCount;       // 구매자 수
    private List<ParticipantResponse> participants;
}