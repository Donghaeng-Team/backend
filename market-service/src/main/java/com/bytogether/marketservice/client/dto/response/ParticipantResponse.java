package com.bytogether.marketservice.client.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class ParticipantResponse {
    private Long userId;
    private String nickname;
    private String profileImage;      // User 서비스에서 조회
    private Boolean isBuyer;          // 구매 의사 확정 여부
    private Boolean isCreator;        // 채팅방 개설자 여부

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime joinedAt;
}
