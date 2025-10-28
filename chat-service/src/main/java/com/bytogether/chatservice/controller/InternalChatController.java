package com.bytogether.chatservice.controller;

import com.bytogether.chatservice.dto.common.ApiResponse;
import com.bytogether.chatservice.dto.request.ChatRoomCreateRequest;
import com.bytogether.chatservice.dto.response.ChatRoomResponse;
import com.bytogether.chatservice.dto.response.ParticipantListResponse;
import com.bytogether.chatservice.dto.response.ParticipantListResponseWrap;
import com.bytogether.chatservice.dto.response.UserMarketIdsResponse;
import com.bytogether.chatservice.entity.ChatRoom;
import com.bytogether.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/create")
    public ChatRoomResponse createChatRoom(@RequestBody ChatRoomCreateRequest request) {
        log.info("internal 채팅방 생성 요청 - request: {}", request);

        return chatRoomService.createRoom(request);
    }

    @GetMapping("/{marketId}/participants")
    public ParticipantListResponse getParticipants(@PathVariable("marketId") Long marketId) {
        // 참가자 목록 정보 쿼리
        log.info("internal 채팅방 참가자 목록 요청 - marketId: {}", marketId);

        return chatRoomService.getParticipants(marketId);
    }

    @PostMapping("/participantList")
    public List<ParticipantListResponseWrap> getParticipantList(@RequestBody List<Long> marketIds) {
        // 참가자 목록 정보 쿼리
        log.info("internal 채팅방 참가자 목록 요청 - marketIds: {}", marketIds);

        return chatRoomService.getParticipantList(marketIds);
    }

    @GetMapping("/mylist/{userId}")
    public UserMarketIdsResponse chatRoomListAndStats(@PathVariable("userId") Long userId) {

        return chatRoomService.getUserMarketIds(userId);
    }
}
