package com.bytogether.chatservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
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

@Slf4j
public class GatewayClaimHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        // 디버깅: 모든 헤더 출력
        log.debug("=== WebSocket Handshake Headers ===");
        request.getHeaders().forEach((key, value) ->
            log.debug("Header: {} = {}", key, value)
        );

        // ServletServerHttpRequest인 경우 원본 요청 정보도 출력
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            log.debug("Request URI: {}", servletRequest.getServletRequest().getRequestURI());
            log.debug("Request Method: {}", servletRequest.getServletRequest().getMethod());
        }

        List<String> userIds = request.getHeaders().get("X-User-Id");

        if (userIds != null && !userIds.isEmpty()) {
            String userId = userIds.get(0);
            log.debug("Found X-User-Id: {}", userId);

            // Principal 인터페이스를 구현하는 간단한 익명객체
            return () -> userId;
        }

        log.warn("X-User-Id header not found in WebSocket handshake");
        return null; // 인증 실패 시 Principal 없음
    }
}