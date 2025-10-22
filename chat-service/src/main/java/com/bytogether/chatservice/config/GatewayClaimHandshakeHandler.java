package com.bytogether.chatservice.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * 웹소켓 핸드셰이크 단계에서 헤더의 userId를 추출하기 위한 인터셉터
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-21
 */

public class GatewayClaimHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        List<String> userIds = request.getHeaders().get("X-User-Id");

        if (userIds != null && !userIds.isEmpty()) {
            String userId = userIds.get(0);

            // Principal 인터페이스를 구현하는 간단한 익명객체
            return () -> userId;
        }

        return null; // 인증 실패 시 Principal 없음
    }
}