package com.bytogether.marketservice.client.dto.response;

import lombok.Getter;

@Getter
public enum ChatRoomStatus {
    RECRUITING("모집중"),
    RECRUITMENT_CLOSED("모집마감"),
    COMPLETED("공동구매 종료"),
    CANCELLED("자동취소");

    private final String description;

    ChatRoomStatus(String description) {
        this.description = description;
    }

}