package com.bytogether.chatservice.config;

import com.bytogether.chatservice.entity.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisMessageSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public void onMessage(String message, String channel) {
        try {
            // Redis에서 받은 메시지를 파싱
            ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);

            // 채널에서 roomId 추출 (예: "chat:room:123")
            String roomId = channel.split(":")[2];

            // 이 Pod에 연결된 WebSocket 클라이언트들에게 전송
            messagingTemplate.convertAndSend(
                    "/topic/rooms." + roomId + ".messages",
                    chatMessage
            );
        } catch (Exception e) {
            log.error("Error processing Redis message", e);
        }
    }
}