package com.bytogether.chatservice.config;

import com.bytogether.chatservice.dto.response.ChatMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 통합 메시지 구독자
 * 다른 Pod에서 발행한 메시지를 받아서 현재 Pod의 WebSocket 클라이언트들에게 전송
 * AOP 자동화에 최적화
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-21
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class UnifiedRedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());
            String body = new String(message.getBody());

            log.debug("Received Redis message from channel: {}", channel);

            // 채널 타입별 처리
            if (channel.startsWith("chat:room:")) {
                // 채팅방 메시지
                handleChatRoomMessage(channel, body);

            } else if (channel.startsWith("user:")) {
                // 사용자별 알림 (강퇴 등)
                handleUserNotification(channel, body);

            } else {
                log.warn("Unknown channel pattern: {}", channel);
            }

        } catch (Exception e) {
            log.error("Failed to process Redis message", e);
        }
    }

    /**
     * 채팅방 메시지 처리
     * chat:room:123 -> /topic.rooms.123.messages
     */
    private void handleChatRoomMessage(String channel, String body) throws Exception {
        // 1. roomId 추출
        String roomId = extractRoomId(channel);

        // 2. JSON -> DTO 변환
        ChatMessageResponse chatMessage = objectMapper.readValue(
                body,
                ChatMessageResponse.class
        );

        // 3. WebSocket으로 전송
        String destination = "/topic.rooms." + roomId + ".messages";
        messagingTemplate.convertAndSend(destination, chatMessage);

        log.debug("Broadcasted message to {} from Redis", destination);
    }

    /**
     * 사용자 알림 처리
     * user:123:kicked -> /queue/notifications
     */
    private void handleUserNotification(String channel, String body) throws Exception {
        // 1. userId 추출
        String userId = extractUserId(channel);

        if (userId == null) {
            log.warn("Cannot extract userId from channel: {}", channel);
            return;
        }

        // 2. 특정 사용자에게만 전송
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                body  // 단순 문자열 메시지
        );

        log.debug("Sent notification to user {} from Redis", userId);
    }

    /**
     * 채널명에서 roomId 추출
     * "chat:room:123" -> "123"
     */
    private String extractRoomId(String channel) {
        String[] parts = channel.split(":");
        if (parts.length >= 3 && "room".equals(parts[1])) {
            return parts[2];
        }
        return null;
    }

    /**
     * 채널명에서 userId 추출
     * "user:123:kicked" -> "123"
     */
    private String extractUserId(String channel) {
        String[] parts = channel.split(":");
        if (parts.length >= 2 && "user".equals(parts[0])) {
            return parts[1];
        }
        return null;
    }
}