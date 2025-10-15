package com.bytogether.chatservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnifiedRedisMessageSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    // 모든 Redis 메시지를 한 곳에서 처리
    @EventListener
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());
            String body = new String(message.getBody());

            // 채널명을 WebSocket 대상으로 변환
            String destination = convertChannelToDestination(channel);

            // 메시지 타입에 따라 다르게 처리
            if (channel.startsWith("redis:topic:")) {
                // 일반 토픽 메시지
                Object payload = objectMapper.readValue(body, Object.class);
                messagingTemplate.convertAndSend(destination, payload);

            } else if (channel.startsWith("redis:user:")) {
                // 특정 사용자 메시지
                String userId = extractUserFromChannel(channel);
                Object payload = objectMapper.readValue(body, Object.class);
                messagingTemplate.convertAndSendToUser(userId, destination, payload);

            } else if (channel.startsWith("redis:queue:")) {
                // 큐 메시지 처리
                handleQueueMessage(destination, body);
            }

            log.debug("Processed Redis message from channel: {} to destination: {}", channel, destination);

        } catch (Exception e) {
            log.error("Error processing Redis message", e);
        }
    }

    private String convertChannelToDestination(String channel) {
        // redis:topic:rooms:123:messages -> /topic/rooms.123.messages
        return channel
                .replace("redis:", "/")
                .replaceAll(":", ".")
                .replaceFirst("\\.", "/");
    }

    // 채널명에서 userId 추출
    private String extractUserFromChannel(String channel) {
        // redis:user:12345:queue:kicked → 12345
        String[] parts = channel.split(":");
        if (parts.length >= 3 && "user".equals(parts[1])) {
            return parts[2];  // userId 반환
        }
        return null;
    }

    // 큐 메시지 처리 (필요에 따라 커스터마이징)
    // TODO: 현재 큐에 해당하는 메시지는 강퇴알림 메시지 -> KickNotification DTO를
    private void handleQueueMessage(String destination, String body) {
//        try {
//            // 큐 타입별 처리
//            if (destination.contains("kicked")) {
//                // 강퇴 메시지 특별 처리
//                KickNotification notification = objectMapper.readValue(body, KickNotification.class);
//                log.info("User kicked from room: {}", notification.getRoomId());
//
//                // 필요시 추가 로직 (예: 세션 정리, 로깅 등)
//            } else {
//                // 기본 큐 메시지 처리
//                Object payload = objectMapper.readValue(body, Object.class);
//                messagingTemplate.convertAndSend(destination, payload);
//            }
//        } catch (Exception e) {
//            log.error("Failed to handle queue message", e);
//        }
    }
}