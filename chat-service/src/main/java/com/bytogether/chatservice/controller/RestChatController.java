package com.bytogether.chatservice.controller;

import com.bytogether.chatservice.dto.common.ApiResponse;
import com.bytogether.chatservice.dto.request.ChatMessagePageRequest;
import com.bytogether.chatservice.dto.request.ChatRoomPageRequest;
import com.bytogether.chatservice.dto.response.*;
import com.bytogether.chatservice.service.ChatMessageService;
import com.bytogether.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
 * v1.04
 * api 및 pathVariable 수정
 * id -> roomId
 *
 * v1.05
 * chatRoomList 메서드 매개변수 간소화
 *
 * @author jhj010311@gmail.com
 * @version 1.05
 * @since 2025-10-16
 */

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class RestChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    /*
        채팅방 기본 CRUD
        ├─ GET    /api/chat                    목록 조회
        └─ GET    /api/chat/{roomId}               개별 채팅창 페이지 접속

        메시지
        └─ GET    /api/chat/{roomId}/messages      메시지 조회

        참가자 관리
        ├─ GET    /api/chat/{roomId}/participants       참가자 목록
        ├─ POST   /api/chat/{roomId}/exit               퇴장
        └─ POST   /api/chat/{roomId}/kick/{userId}      강퇴

        공동구매 액션
        ├─ POST   /api/chat/{roomId}/participate        구매 의사 확정
        ├─ DELETE /api/chat/{roomId}/participate        구매 의사 취소
        ├─ PATCH  /api/chat/{roomId}/extend             기한 연장
        ├─ PATCH  /api/chat/{roomId}/close              모집 마감
        └─ POST   /api/chat/{roomId}/complete           구매 확정
    * */

    // TODO: 프론트 페이지와 직결되는 각종 REST API를 작성하기
    // note: 리턴값은 dto.common.ApiResponse로 통일

    @GetMapping
    public ResponseEntity<ApiResponse<ChatRoomPageResponse>> chatRoomList(ChatRoomPageRequest request,
                                                                          @RequestHeader("X-User-Id") Long userId) {
        // TODO: 로그인한 유저의 id를 사용하여 유저가 참가한 적 있는 모든 채팅방 리스트를 쿼리
        // 현재 공동구매에 참가한 인원 정보도 넣어줘야 함

        ChatRoomPageResponse chatRoomList = null;

        if(request.getCursor() == null){
            chatRoomList = chatRoomService.getMyChatRooms(userId, request.getSize());
        } else {
            chatRoomList = chatRoomService.getMyChatRooms(userId, request.getCursor(), request.getParticipantId(), request.getSize());
        }


        return ResponseEntity.ok(ApiResponse.success(chatRoomList));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> enterChatRoom(@PathVariable("roomId") Long chatRoomId, @RequestHeader("X-User-Id") Long userId) {
        // TODO: 채팅방 id를 사용하여 참가중인 특정 채팅방 페이지를 오픈

        if(chatRoomService.isParticipating(chatRoomId, userId)){
            return ResponseEntity.ok(ApiResponse.success(chatRoomService.getChatRoomDetails(chatRoomId)));
        }

        return ResponseEntity.badRequest().body(ApiResponse.fail("잘못된 요청입니다"));
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<ChatMessagePageResponse>> getMessages(ChatMessagePageRequest request,
                                                            @RequestHeader("X-User-Id") Long userId) {

        ChatMessagePageResponse chatMessagePageResponse;

        if(request.getCursor() == null){
            chatMessagePageResponse = chatMessageService.getRecentMessages(request.getRoomId(), userId, request.getSize());
        } else {
            chatMessagePageResponse = chatMessageService.getMessagesBeforeCursor(request.getRoomId(), userId, request.getCursor(), request.getSize());
        }

        return ResponseEntity.ok(ApiResponse.success(chatMessagePageResponse));
    }

    @GetMapping("/{roomId}/participants")
    public ResponseEntity<ApiResponse<ParticipantListResponse>> getParticipants(@PathVariable("roomId") Long roomId) {
        // TODO: 참가자 목록 정보 쿼리

        return ResponseEntity.ok(ApiResponse.success(chatRoomService.getParticipants(roomId)));
    }

    @PostMapping("/{roomId}/exit")
    public ResponseEntity<ApiResponse<String>> leaveChatRoom(@PathVariable("roomId") Long chatRoomId) {
        // TODO: 채팅방 탈퇴 처리
        // String 말고 ChatRoomExitResponse 사용

        // 1. 채팅방 참가자인지 검증
        // 2. 채팅방 방장인지 확인하고 처리 분기
        // 2-1. 방장이라면 채팅방 폐쇄 처리 병행
        // 2-2. 일반 참가자라면 공동구매 참여자인지 확인해서 공동구매는 참가취소 처리
        // 3. 채팅방 탈퇴
        // 4. 탈퇴 시간, 시스템 메시지 담아 반환

        return null;
    }

    @PostMapping("/{roomId}/kick")
    public ResponseEntity<ApiResponse<String>> kickParticipant(@PathVariable("roomId") Long chatRoomId,
                                               @RequestParam Long targetUserId,
                                               @RequestHeader("X-User-Id") Long requesterId) {
        // TODO: 참가자 강퇴 처리 및 참가자 정보 담아서 반환
        // String 말고 말고 ChatRoomKickResponse 사용

        // TODO: 방장 인증 필요

        // 1. 방장 권한 인증
        // 2. 강퇴 처리
        // 3. 강퇴 대상에게 메시지 전달(stomp 컨트롤러 api로 처리?)
        // 4. 강퇴한 유저 정보, 강퇴 시간, 시스템 메시지 담아 반환

        return null;
    }

    @PostMapping("/{roomId}/participate")
    public ResponseEntity<ApiResponse<BuyerConfirmResponse>> confirmBuyer(@PathVariable("roomId") Long chatRoomId,
                                                          @RequestHeader("X-User-Id") Long userId) {
        // TODO: 공동구매 참가시 처리 후 해당 정보 반환

        // 1. 채팅방 참가자인지, 이미 공동구매에 참가중인지 검증
        // 2. 공동구매 참가 처리
        // 3. BuyerConfirmResponse 반환

        return null;
    }

    @DeleteMapping("/{roomId}/participate")
    public ResponseEntity<ApiResponse<BuyerConfirmResponse>> cancelBuyer(@PathVariable("roomId") Long chatRoomId,
                                                         @RequestHeader("X-User-Id") Long userId) {
        // TODO: 공동구매 참가 취소시 처리 후 해당 정보 반환

        // 1. 채팅방 참가자인지, 공동구매에 참가중인게 맞는지 검증
        // 2. 공동구매 불참 처리
        // 3. BuyerConfirmResponse 반환

        return null;
    }

    @PatchMapping("/{roomId}/extend")
    public ResponseEntity<ApiResponse<ExtendDeadlineResponse>> extendDeadline(@PathVariable("roomId") Long chatRoomId,
                                              @RequestParam Integer hours,  // 연장할 시간
                                              @RequestHeader("X-User-Id") Long userId) {
        // TODO: 방장이 채팅방 마감기한을 연장신청할 경우의 처리
        // TODO: 방장 인증 필요

        // 1. 방장 검증
        // 2. 지정된 시간만큼 마감기한 연장
        // 3. 채팅방에 마감기한 연장 시스템 메시지 발송
        // 4. ExtendDeadlineResponse 반환

        return null;
    }

    @PatchMapping("/{roomId}/close")
    public ResponseEntity<ApiResponse<RecruitmentCloseResponse>> closeRecruitment(@PathVariable("roomId") Long chatRoomId,
                                                @RequestHeader("X-User-Id") Long userId) {
        // TODO: 방장이 직접 공동구매를 마감신청하는 케이스의 처리를 담당
        // 방장이 직접, 혹은 마감기한이 지나면 시스템에 의해 자동으로 공동구매 모집을 마감
        // 시스템 자동처리 쪽은 따로 구현?

        // TODO: 방장 인증 필요

        // 1. 방장 검증
        // 2. 공동구매 참가자가 아닌 기타 채팅방 참가자를 강퇴
        // 3. 채팅방에 공동구매 마감 시스템 메시지 발송
        // 4. RecruitmentCloseResponse 반환

        return null;
    }

    @PostMapping("/{roomId}/complete")
    public ResponseEntity<ApiResponse<String>> confirmPurchase(@PathVariable("roomId") Long chatRoomId) {
        // TODO: 모든 활동이 종료된 이후 방장이 채팅방 기능 정지 처리
        // String 말고 ChatRoomConfirmResponse 사용
        // 모집 마감기한으로부터 시간이 일정 기간 이상 지나면 시스템이 자동으로 처리하는 기능 추가 고려

        // 1. 방장 검증
        // 2. 채팅방에 공동구매 종료 시스템 메시지 발송
        // 3. 더 이상 채팅방에 채팅을 보낼 수 없도록 처리(원하는 경우 각 참가자별로 퇴장은 가능)
        // 4. ChatRoomConfirmResponse 반환

        return null;
    }
}