package com.bytogether.chatservice.controller;

import com.bytogether.chatservice.dto.common.ApiResponse;
import com.bytogether.chatservice.dto.response.*;
import com.bytogether.chatservice.entity.ChatMessage;
import com.bytogether.chatservice.repository.ChatMessageRepository;
import com.bytogether.chatservice.repository.ChatRoomRepository;
import com.bytogether.chatservice.service.ChatMessageService;
import com.bytogether.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅방 목록을 조회하고 채팅방 메시지를 확인
 * 그 외 공동구매 관련 액션을 처리하는 컨트롤러
 *
 * v1.02
 * 계획 확인용 수도코드 작성
 *
 * v1.03
 * 리턴값을 ResponseEntity로 수정
 *
 * @author jhj010311@gmail.com
 * @version 1.03
 * @since 2025-10-13
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

        메시지
        └─ GET    /api/chat/{id}/messages      메시지 조회

        참가자 관리
        ├─ GET    /api/chat/{id}/participants  참가자 목록
        ├─ POST   /api/chat/{id}/leave         퇴장
        └─ POST   /api/chat/{id}/kick          강퇴

        공동구매 액션
        ├─ POST   /api/chat/{id}/confirm-buyer        구매 의사 확정
        ├─ POST   /api/chat/{id}/cancel--buyer        구매 의사 취소
        ├─ POST   /api/chat/{id}/extend-deadline      기한 연장
        ├─ POST   /api/chat/{id}/close-recruitment    모집 마감
        └─ POST   /api/chat/{id}/confirm-purchase     구매 확정
    * */

    // TODO: 프론트 페이지와 직결되는 각종 REST API를 작성하기
    // note: 리턴값은 dto.common.ApiResponse로 통일

    @GetMapping
    public ResponseEntity<ApiResponse<ChatRoomListPageResponse>> chatRoomList(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
                                                                            @RequestParam(required = false) Long participantId,
                                                                            @RequestParam(defaultValue = "20") int size,
                                                                            @RequestHeader("X-User-Id") Long userId) {
        // TODO: 로그인한 유저의 id를 사용하여 유저가 참가한 적 있는 모든 채팅방 리스트를 쿼리
        // 현재 공동구매에 참가한 인원 정보도 넣어줘야 함

        ChatRoomListPageResponse chatRoomList = null;

        if(cursor == null){
            chatRoomList = chatRoomService.getMyChatRooms(userId, size);
        } else {
            chatRoomList = chatRoomService.getMyChatRooms(userId, cursor, participantId, size);
        }


        ApiResponse<ChatRoomListPageResponse> response = new ApiResponse<>(true, "success", chatRoomList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> enterChatRoom(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        // TODO: 채팅방 id를 사용하여 개별 채팅방을 오픈

        if(chatRoomService.enterChatRoom(id, userId)){

        }


        return null;
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<ChatMessagePageResponse>> getMessages(@PathVariable("id") Long chatRoomId,
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

    @GetMapping("/{id}/participants")
    public ResponseEntity<ApiResponse<ParticipantListResponse>> getParticipants(@RequestBody @PathVariable("id") Long chatRoomId) {
        // TODO: 참가자 목록 정보 쿼리


        return null;
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<String>> leaveChatRoom(@PathVariable("id") Long chatRoomId) {
        // TODO: 채팅방 탈퇴 처리 및 탈퇴한 채팅방 정보 담아서 반환
        // String 말고 좋은 방법 있는지 검토

        return null;
    }

    @PostMapping("/{id}/kick")
    public ResponseEntity<ApiResponse<String>> kickParticipant(@PathVariable("id") Long chatRoomId,
                                               @RequestParam Long targetUserId,
                                               @RequestHeader("X-User-Id") Long requesterId) {
        // TODO: 참가자 강퇴 처리 및 참가자 정보 담아서 반환
        // String 말고 좋은 방법 있는지 검토

        // TODO: 방장 인증 필요

        return null;
    }

    @PostMapping("/{id}/confirm-buyer")
    public ResponseEntity<ApiResponse<BuyerConfirmResponse>> confirmBuyer(@PathVariable("id") Long chatRoomId,
                                                          @RequestHeader("X-User-Id") Long userId) {
        // TODO: 공동구매 참가시 처리 후 해당 정보 반환

        return null;
    }

    @PostMapping("/{id}/cancel-buyer")
    public ResponseEntity<ApiResponse<BuyerConfirmResponse>> cancelBuyer(@PathVariable("id") Long chatRoomId,
                                                         @RequestHeader("X-User-Id") Long userId) {
        // TODO: 공동구매 참가 취소시 처리 후 해당 정보 반환

        return null;
    }

    @PostMapping("/{id}/extend-deadline")
    public ResponseEntity<ApiResponse<ExtendDeadlineResponse>> extendDeadline(@PathVariable("id") Long chatRoomId,
                                              @RequestParam Integer hours,  // 연장할 시간
                                              @RequestHeader("X-User-Id") Long userId) {
        // TODO: 방장이 채팅방 마감기한을 연장신청할 경우의 처리
        // TODO: 방장 인증 필요

        return null;
    }

    @PostMapping("/{id}/close-recruitment")
    public ResponseEntity<ApiResponse<RecruitmentCloseResponse>> closeRecruitment(@PathVariable("id") Long chatRoomId,
                                                @RequestHeader("X-User-Id") Long userId) {
        // TODO: 방장이 직접 공동구매를 마감신청하는 케이스의 처리를 담당
        // 방장이 직접, 혹은 마감기한이 지나면 자동으로 공동구매 모집을 마감

        // TODO: 방장 인증 필요

        return null;
    }

    @PostMapping("/{id}/confirm-purchase")
    public ResponseEntity<ApiResponse<String>> confirmPurchase(@PathVariable("id") Long chatRoomId) {
        // TODO: 채팅방 탈퇴 처리 및 탈퇴한 채팅방 정보 담아서 반환
        // String 말고 좋은 방법 있는지 검토

        return null;
    }
}