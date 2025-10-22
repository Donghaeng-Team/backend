package com.bytogether.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 채팅방의 참가인원 목록 정보를 전달하는 dto
 *
 * 1.01
 * 필드명 변경
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-17
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantListResponse {
    private Integer currentParticipants;        // 총 참가자 수
    private Integer currentBuyers;              // 구매자 수
    private List<ParticipantResponse> participants;
}