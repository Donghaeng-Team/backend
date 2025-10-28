package com.bytogether.chatservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipantListResponseWrap {
    Long requestMarketId;
    private ParticipantListResponse participantListResponse;
}
