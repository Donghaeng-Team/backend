package com.bytogether.chatservice.entity;

import lombok.Getter;

@Getter
public enum ExitType {
    VOLUNTARY("자발적 탈퇴"),
    NOT_BUYER("미참여자 자동퇴장"),
    KICKED("강퇴"),
    COMPLETED("정상종료");

    private final String description;

    ExitType(String description) {
        this.description = description;
    }

}