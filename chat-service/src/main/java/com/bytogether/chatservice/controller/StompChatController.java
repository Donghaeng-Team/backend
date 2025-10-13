package com.bytogether.chatservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

/**
 * 채팅에 관련된 실시간 구독 메커니즘을 담당하는 컨트롤러
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-13
 */

@Controller
@RequiredArgsConstructor
public class StompChatController {

    // TODO: STOMP 클라이언트에 관한 설정을 관리
    // TODO: 채팅방 개설 요청을 rabbitMQ가 아닌 내부api로 받게 될 수도 있음

    // topic : 구독신청
    // 새 채팅 알림 - /rooms.notifications

    // app : 즉각요청
}
