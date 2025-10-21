package com.bytogether.chatservice.controller;

import com.bytogether.chatservice.dto.request.ChatMessageSendRequest;
import com.bytogether.chatservice.dto.response.KickNotificationResponse;
import com.bytogether.chatservice.entity.ChatMessage;
import com.bytogether.chatservice.entity.ChatRoomParticipant;
import com.bytogether.chatservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * 채팅에 관련된 실시간 구독 메커니즘을 담당하는 컨트롤러
 *
 * 1.02
 * 수도코드 기초 완성
 * 샘플을 겸하는 sendMessage 메서드 작성
 *
 * 1.03
 * 불필요한 topic 관련 코드 제거
 *
 * @author jhj010311@gmail.com
 * @version 1.03
 * @since 2025-10-20
 */

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final ChatMessageService chatMessageService;

    // STOMP 클라이언트에 관한 설정을 관리

    // 구독 (Topic)
    // /topic.rooms.{roomId}.messages       // 일반 + 시스템 메시지 통합
    // /user/{userId}/queue/notifications   // 강퇴 등 개인 알림

    // 요청 (App)
    // /app/chat.{roomId}.sendMessage       // 메시지 전송
    // /app/chat.{roomId}.kick.{targetUserId} // 강퇴

    @MessageMapping("chat.{roomId}.sendMessage")
    public void sendMessage(@DestinationVariable Long roomId,
                            @Payload ChatMessageSendRequest message,
                            Principal principal) {
        Long senderUserId = Long.parseLong(principal.getName());

        log.info("유저 채팅 전송 - userId: {}, roomId: {}, message: {}", senderUserId, roomId, message.getMessageContent());

        chatMessageService.sendMessage(roomId, senderUserId, message);

    }

    @MessageMapping("chat.{roomId}.leave")
    public void leaveRoom(@DestinationVariable Long roomId, Principal principal) {

        Long userId = Long.parseLong(principal.getName());
        log.info("유저 채팅방 연결해제 - userId: {}, roomId: {}", userId, roomId);

        chatMessageService.handleLeaveRoom(roomId, userId);
    }
}
