package com.bytogether.chatservice.entity;

import lombok.Getter;

@Getter
public enum ParticipantStatus {
    ACTIVE("활동중", true),
    LEFT_VOLUNTARY("자발적 탈퇴", false),
    LEFT_RECRUITMENT_CLOSED("모집마감 자동퇴장", true),  // 구매자가 아닌 사람 퇴장
    LEFT_RECRUITMENT_CANCELED("채팅방 중도종료", false),  // 방장이 채팅방 종료
    LEFT_COMPLETED("공동구매 완료", true),
    BANNED("강퇴", false);

    private final String description;
    private final boolean isViewable;

    ParticipantStatus(String description, boolean isViewable) {
        this.description = description;
        this.isViewable = isViewable;
    }

}