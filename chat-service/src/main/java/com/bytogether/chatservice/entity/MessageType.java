package com.bytogether.chatservice.entity;

import lombok.Getter;

@Getter
public enum MessageType {
    TEXT("일반 메시지"),
    SYSTEM("시스템 메시지"),
    DEADLINE_EXTEND("기한연장 알림");

    private final String description;

    MessageType(String description) {
        this.description = description;
    }

}