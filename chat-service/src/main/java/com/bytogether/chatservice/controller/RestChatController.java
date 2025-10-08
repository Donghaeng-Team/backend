package com.bytogether.chatservice.controller;

import com.bytogether.chatservice.entity.ChatMessage;
import com.bytogether.chatservice.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅방 목록을 조회하고 채팅방 메시지를 확인
 * 그 외 공동구매 관련 액션을 처리하는 컨트롤러
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-08
 */

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class RestChatController {

    /*
        채팅방 기본 CRUD
        ├─ GET    /api/chat                    목록 조회
        ├─ GET    /api/chat/{id}               개별 채팅창 페이지 접속

        참가자 관리
        ├─ POST   /api/chat/{id}/join          입장
        ├─ POST   /api/chat/{id}/leave         퇴장
        ├─ GET    /api/chat/{id}/participants  참가자 목록
        └─ POST   /api/chat/{id}/kick          강퇴

        메시지
        └─ GET    /api/chat/{id}/messages      메시지 조회

        공동구매 액션
        ├─ POST   /api/chat/{id}/confirm-buyer        구매 의사 확정
        ├─ POST   /api/chat/{id}/extend-deadline      기한 연장
        ├─ POST   /api/chat/{id}/close-recruitment    모집 마감
        └─ POST   /api/chat/{id}/confirm-purchase     구매 확정
    * */
}