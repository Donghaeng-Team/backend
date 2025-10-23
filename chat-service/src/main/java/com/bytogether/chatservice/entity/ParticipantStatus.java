package com.bytogether.chatservice.entity;

import lombok.Getter;

@Getter
public enum ParticipantStatus {
    ACTIVE("활동중"),
    LEFT_VOLUNTARY("자발적 탈퇴"),
    LEFT_RECRUITMENT_CLOSED("모집마감 자동퇴장"),  // 구매자가 아닌 사람 퇴장
    LEFT_RECRUITMENT_CANCELED("채팅방 종료"),                // 방장이 채팅방 종료
    LEFT_COMPLETED("정상종료"),
    BANNED("강퇴");

    private final String description;

    ParticipantStatus(String description) {
        this.description = description;
    }

}