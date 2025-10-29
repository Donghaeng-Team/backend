package com.bytogether.chatservice.dto.common;

import com.bytogether.chatservice.entity.ChatRoomStatus;
import com.bytogether.chatservice.entity.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessagePermission {
    private ChatRoomStatus roomStatus;
    private ParticipantStatus participantStatus;
    private Boolean isBuyer;

    public boolean canSendMessage() {
        // 참가자가 ACTIVE가 아니면 불가
        if (participantStatus != ParticipantStatus.ACTIVE) {
            return false;
        }

        // 채팅방 상태별 권한 체크
        return switch (roomStatus) {
            case RECRUITING -> true;
            case RECRUITMENT_CLOSED -> isBuyer;  // 구매자만 가능
            case COMPLETED, CANCELLED -> false;   // 모두 불가
            default -> false;
        };
    }

    public String getDenialReason() {
        if (participantStatus != ParticipantStatus.ACTIVE) {
            return "채팅방에서 퇴장한 상태입니다";
        }

        return switch (roomStatus) {
            case RECRUITING -> null;
//            case RECRUITMENT_CLOSED -> !isBuyer ?
//                    "모집이 마감되어 구매자만 메시지를 보낼 수 있습니다" : null;
//            case COMPLETED -> "완료된 채팅방입니다";
            case COMPLETED -> !isBuyer ?
                    "모집이 마감되어 구매자만 메시지를 보낼 수 있습니다" : null;
            case CANCELLED -> "취소된 채팅방입니다";
            default -> "채팅을 보낼 수 없는 상태입니다";
        };
    }
}
