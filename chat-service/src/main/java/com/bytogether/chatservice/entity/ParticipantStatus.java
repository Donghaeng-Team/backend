package com.bytogether.chatservice.entity;

import lombok.Getter;

@Getter
public enum ParticipantStatus {
    ACTIVE("활동중"),
    LEFT_VOLUNTARY("자발적 탈퇴"),
    LEFT_NOT_BUYER("미참여자 자동퇴장"),
    LEFT_COMPLETED("정상종료"),
    BANNED("강퇴");

    private final String description;

    ParticipantStatus(String description) {
        this.description = description;
    }

}