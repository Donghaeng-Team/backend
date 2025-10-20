package com.bytogether.chatservice.controller;

import com.bytogether.chatservice.dto.common.ApiResponse;
import com.bytogether.chatservice.dto.request.ChatRoomCreateRequest;
import com.bytogether.chatservice.dto.response.ChatRoomResponse;
import com.bytogether.chatservice.entity.ChatRoom;
import com.bytogether.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * msa 내부에서 작동하는 api 담당 컨트롤러
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-20
 */

@Slf4j
@RestController
@RequestMapping("/internal/v1/chat")
@RequiredArgsConstructor
public class InternalChatController {

    private final ChatRoomService chatRoomService;

    // 채팅방 생성 요청 api
    @PostMapping("/internal/chat-rooms")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createChatRoom(@RequestBody ChatRoomCreateRequest request) {
        log.info("채팅방 생성 요청 - request: {}", request);

        ChatRoomResponse createdRoom = chatRoomService.createRoom(request);

        return ResponseEntity.ok(ApiResponse.success(createdRoom));
    }
}
