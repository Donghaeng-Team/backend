package com.bytogether.marketservice.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ParticipantListResponse {
    private Long roomId;               // 채팅방 ID
    private Integer currentParticipants;        // 총 참가자 수
    private Integer currentBuyers;              // 구매자 수
    private List<ParticipantResponse> participants;

}
