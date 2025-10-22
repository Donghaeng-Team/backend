package com.bytogether.chatservice.controller;

import com.bytogether.chatservice.dto.common.ApiResponse;
import com.bytogether.chatservice.dto.response.ParticipantListResponse;
import com.bytogether.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat/public")
@RequiredArgsConstructor
public class PublicRestChatController {

    private final ChatRoomService chatRoomService;

    /*
        참가자 목록
        GET    /api/v1/chat/public/{marketId}/participants
    */

    /*
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<ApiResponse<ParticipantListResponse>> getParticipants(@PathVariable("roomId") Long roomId) {
        // 참가자 목록 정보 쿼리
        log.info("채팅방 참가자 목록 요청 - roomId: {}", roomId);

        return ResponseEntity.ok(ApiResponse.success(chatRoomService.getParticipants(roomId)));
    }

     */

    @GetMapping("/{marketId}/participants")
    public ResponseEntity<ApiResponse<ParticipantListResponse>> getParticipants(@PathVariable("marketId") Long marketId) {
        // 참가자 목록 정보 쿼리
        log.info("채팅방 참가자 목록 요청 - marketId: {}", marketId);

        return ResponseEntity.ok(ApiResponse.success(chatRoomService.getParticipants(marketId)));
    }
}
