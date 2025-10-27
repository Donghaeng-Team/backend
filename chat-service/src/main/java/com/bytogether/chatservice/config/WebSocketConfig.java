package com.bytogether.chatservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;


/**
 * 웹소켓 설정
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-07
 */

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // STOMP가 사용할 엔드포인트 지정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ws://localhost:8080/ws-chat 으로 연결
        registry.addEndpoint("/ws/v1/chat/private")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        log.debug("========================================");
                        log.debug("🤝 WebSocket Handshake 시작");
                        log.debug("========================================");
                        log.debug("📍 URI: {}", request.getURI());

                        // 모든 헤더 출력
                        log.debug("📋 Headers:");
                        request.getHeaders().forEach((key, value) ->
                            log.debug("  - {}: {}", key, value)
                        );

                        // X-User-Id를 attributes에 저장하여 STOMP 세션에서 사용 가능하도록
                        java.util.List<String> userIds = request.getHeaders().get("X-User-Id");
                        if (userIds != null && !userIds.isEmpty()) {
                            String userId = userIds.get(0);
                            attributes.put("userId", userId);
                            log.debug("💾 userId를 세션 attributes에 저장: {}", userId);
                        } else {
                            log.warn("⚠️ X-User-Id 헤더 없음");
                        }

                        // ServletRequest인 경우 쿠키 정보도 출력
                        if (request instanceof ServletServerHttpRequest) {
                            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                            var cookies = servletRequest.getServletRequest().getCookies();
                            if (cookies != null) {
                                log.debug("🍪 Cookies:");
                                for (var cookie : cookies) {
                                    log.debug("  - {}: {}", cookie.getName(), cookie.getValue());
                                }
                            }
                        }

                        log.debug("========================================");
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               WebSocketHandler wsHandler, Exception exception) {
                        if (exception != null) {
                            log.error("❌ Handshake 실패: ", exception);
                        } else {
                            log.debug("✅ Handshake 성공");
                        }
                    }
                })
                .setHandshakeHandler(new GatewayClaimHandshakeHandler())
                .withSockJS()
                .setSuppressCors(true);  // CORS 헤더 중복 방지 - Gateway에서 처리
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 prefix
        // STOMP가 이 접두사가 붙은 주소로 구독을 신청하고, 채팅 서비스가 이 주소로 발송
        registry.enableSimpleBroker("/topic", "/queue");

        // 메시지 발행 prefix
        // STOMP가 즉시 무언가를 해달라고 채팅 서비스에 요구할 수 있는 주소 접두사
        registry.setApplicationDestinationPrefixes("/app");

        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new org.springframework.messaging.support.ChannelInterceptor() {
            @Override
            public org.springframework.messaging.Message<?> preSend(
                    org.springframework.messaging.Message<?> message,
                    org.springframework.messaging.MessageChannel channel) {

                org.springframework.messaging.simp.stomp.StompHeaderAccessor accessor =
                        org.springframework.messaging.support.MessageHeaderAccessor.getAccessor(
                                message, org.springframework.messaging.simp.stomp.StompHeaderAccessor.class);

                if (accessor != null) {
                    // CONNECT 메시지에서 Principal 설정
                    if (org.springframework.messaging.simp.stomp.StompCommand.CONNECT.equals(accessor.getCommand())) {
                        log.debug("========================================");
                        log.debug("🔐 STOMP CONNECT - Principal 설정 시도");
                        log.debug("========================================");
                        log.debug("Session ID: {}", accessor.getSessionId());

                        // 세션 attributes에서 userId 가져오기
                        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                        log.debug("📦 Session Attributes: {}", sessionAttributes);

                        String userId = null;
                        if (sessionAttributes != null && sessionAttributes.containsKey("userId")) {
                            userId = (String) sessionAttributes.get("userId");
                            log.debug("✅ Session Attributes에서 userId 발견: {}", userId);
                        } else {
                            // Native headers에서도 시도
                            java.util.List<String> userIds = accessor.getNativeHeader("X-User-Id");
                            log.debug("🔍 Native Headers: {}", accessor.toNativeHeaderMap());

                            if (userIds != null && !userIds.isEmpty()) {
                                userId = userIds.get(0);
                                log.debug("✅ Native Header에서 X-User-Id 발견: {}", userId);
                            }
                        }

                        if (userId != null) {
                            // Principal 설정
                            String finalUserId = userId;
                            java.security.Principal principal = () -> finalUserId;
                            accessor.setUser(principal);
                            log.debug("✅ Principal 설정 완료: {}", userId);
                        } else {
                            log.error("❌ userId를 찾을 수 없습니다!");
                        }
                        log.debug("========================================");
                    }
                    // CONNECT 이후의 모든 메시지에서도 Principal 설정 (CONNECT 시 저장된 값 사용)
                    else if (accessor.getUser() == null && accessor.getSessionAttributes() != null) {
                        String userId = (String) accessor.getSessionAttributes().get("userId");
                        if (userId != null) {
                            String finalUserId = userId;
                            java.security.Principal principal = () -> finalUserId;
                            accessor.setUser(principal);
                            log.debug("🔄 Principal 복원 (session={}, userId={})", accessor.getSessionId(), userId);
                        }
                    }
                }

                return message;
            }
        });
    }
}
