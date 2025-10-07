package com.bytogether.chatservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/**
 * 웹소켓 설정
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-07
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // STOMP가 사용할 엔드포인트 지정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ws://localhost:8080/ws-chat 으로 연결
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 prefix
        // STOMP가 이 접두사가 붙은 주소로 구독을 신청하고, 채팅 서비스가 이 주소로 발송
        registry.enableSimpleBroker("/topic");

        // 메시지 발행 prefix
        // STOMP가 즉시 무언가를 해달라고 채팅 서비스에 요구할 수 있는 주소 접두사
        registry.setApplicationDestinationPrefixes("/app");
    }
}
