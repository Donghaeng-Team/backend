package com.bytogether.chatservice.controller;

import com.bytogether.chatservice.dto.common.ApiResponse;
import com.bytogether.chatservice.dto.response.ChatMessagePageResponse;
import com.bytogether.chatservice.dto.response.ChatMessageResponse;
import com.bytogether.chatservice.dto.response.ChatRoomResponse;
import com.bytogether.chatservice.entity.ChatMessage;
import com.bytogether.chatservice.repository.ChatMessageRepository;
import com.bytogether.chatservice.repository.ChatRoomRepository;
import com.bytogether.chatservice.service.ChatMessageService;
import com.bytogether.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅방 목록을 조회하고 채팅방 메시지를 확인
 * 그 외 공동구매 관련 액션을 처리하는 컨트롤러
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-09
 */

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class RestChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

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

    // TODO: 프론트 페이지와 직결되는 각종 REST API를 작성하기
    // note: 리턴값은 dto.common.ApiResponse로 통일

    @GetMapping
    public ApiResponse<List<ChatRoomResponse>> chatRoomList() {
        // TODO: 로그인한 유저의 id를 사용하여 유저가 참가한 활성 상태의 채팅방 리스트를 쿼리
        // 현재 공동구매에 참가한 인원 정보도 넣어줘야 함

        return null;
    }

    @GetMapping("/{id}")
    public ApiResponse<ChatRoomResponse> enterChatRoom(@PathVariable String id) {
        // TODO: 채팅방 id를 사용하여 개별 채팅방을 오픈

        return null;
    }

    @GetMapping("/{id}/messages")
    public ApiResponse<ChatMessagePageResponse> getMessages(@PathVariable("id") Long chatRoomId,
                                                            @RequestParam(required = false) Long cursor,
                                                            @RequestParam(defaultValue = "50") int size,
                                                            @RequestHeader("X-User-Id") Long userId) {

        ChatMessagePageResponse chatMessagePageResponse = new ChatMessagePageResponse();

        if(cursor == null){
            chatMessagePageResponse = chatMessageService.getRecentMessages(chatRoomId, userId, size);
        } else {
            chatMessagePageResponse = chatMessageService.getMessagesBeforeCursor(chatRoomId, userId, cursor, size);
        }

        return null;
    }
}