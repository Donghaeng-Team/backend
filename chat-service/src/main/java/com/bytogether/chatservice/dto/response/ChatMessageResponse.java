package com.bytogether.chatservice.dto.response;

import com.bytogether.chatservice.entity.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅 메세지를 전달하기 위한 리스폰스 클래스
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-08
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long senderId;
    private String senderNickname;  // User 서비스에서 조회 필요
    private String messageContent;
    private MessageType messageType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;
}
