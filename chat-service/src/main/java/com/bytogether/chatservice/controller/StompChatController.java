package com.bytogether.chatservice.controller;

import com.bytogether.chatservice.dto.response.KickNotificationResponse;
import com.bytogether.chatservice.entity.ChatMessage;
import com.bytogether.chatservice.entity.ChatRoomParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * 채팅에 관련된 실시간 구독 메커니즘을 담당하는 컨트롤러
 *
 * 1.02
 * 수도코드 기초 완성
 * 샘플을 겸하는 sendMessage 메서드 작성
 *
 *
 * @author jhj010311@gmail.com
 * @version 1.02
 * @since 2025-10-15
 */

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    // TODO: STOMP 클라이언트에 관한 설정을 관리
    // TODO: 채팅방 개설 요청을 rabbitMQ가 아닌 내부api로 받게 될 수도 있음

    // topic : 구독신청
    // 새 채팅 알림 - /rooms.notifications
    // 채팅방 메시지 구독 - /rooms.{roomId}.messages
    // 채팅방 참여자 구독 - /rooms.{roomId}.participants
    // 채팅방 시스템 메시지 구독 - /rooms.{roomId}.system
    // 강제퇴출 구독 - /user.{userId}.queue.kicked

    // app : 즉각요청
    // 채팅방 최초참가 - /chat.{roomId}.join
    // 메시지 전송 - /chat.{roomId}.sendMessage
    // 채팅방 퇴장(일시적 닫기 및 영구퇴장) - /chat.{roomId}.leave
    // (방장)채팅 참여자 강퇴 - /chat.{roomId}.kick.{userId}

    // TODO: 추후 Payload는 dto로 변경


    //region topics
    @MessageMapping("/rooms.notifications")
    public void notificationSubscribe(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        // TODO: 채팅방 목록에서 새 채팅 알림을 구독
        // dto 클래스 변경?
    }

    @MessageMapping("/rooms.{roomId}.messages")
    public void messageSubscribe(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
        // TODO: 채팅방에서 새 채팅을 구독
    }

    @MessageMapping("/rooms.{roomId}.participants")
    public void participantSubscribe(@DestinationVariable String roomId, @Payload ChatRoomParticipant participant) {
        // TODO: 채팅방에서 참가자 변동을 구독
    }

    @MessageMapping("/rooms.{roomId}.system")
    public void systemSubscribe(@DestinationVariable String roomId, @Payload ChatMessage systemMessage) {
        // TODO: 채팅방에서 시스템 메시지를 구독
    }

    @MessageMapping("/user.{userId}.queue.kicked")
    public void kickSubscribe(@DestinationVariable String userId, @Payload KickNotificationResponse kickNotification) {
        // TODO: 강제퇴장 시스템메시지를 구독
    }
    //endregion

    //region apps
    @MessageMapping("/chat.{roomId}.join")
    public void joinRoom(@DestinationVariable Long roomId, @Payload ChatMessage message) {
        // TODO: 채팅방 최초입장 처리 -> 시스템 메시지 발송
    }

    @MessageMapping("/chat.{roomId}.sendMessage")
    public void sendMessage(@DestinationVariable Long roomId,
                            @Payload ChatMessage message) {
        // 1. 현재 Pod의 클라이언트들에게 전송
        messagingTemplate.convertAndSend(
                "/topic/rooms." + roomId + ".messages",
                message
        );

        // 2. Redis Pub/Sub으로 다른 Pod들에게도 전파
        redisTemplate.convertAndSend("chat:room:" + roomId, message);
    }

    @MessageMapping("/chat.{roomId}.leave")
    public void leaveRoom(@DestinationVariable Long roomId, @Payload ChatMessage message) {
        // TODO: 채팅방 퇴장처리, 창 닫기를 통한 일시 접속해제와 영구퇴장 어느쪽도 공통으로 필요한 세션 만료 처리
    }

    @MessageMapping("/chat.{roomId}.kick.{userId}")
    public void kick(@DestinationVariable Long roomId, @DestinationVariable Long userId, @Payload ChatMessage message) {
        // TODO: 방장이 특정 유저를 강제퇴장시킨 경우, 시스템메시지 발송
    }
    //endregion
}
