package com.bytogether.chatservice.controller;

import com.bytogether.chatservice.dto.common.ApiResponse;
import com.bytogether.chatservice.dto.request.ChatMessagePageRequest;
import com.bytogether.chatservice.dto.request.ChatRoomPageRequest;
import com.bytogether.chatservice.dto.response.*;
import com.bytogether.chatservice.service.ChatMessageService;
import com.bytogether.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 채팅방 목록을 조회하고 채팅방 메시지를 확인
 * 그 외 공동구매 관련 액션을 처리하는 컨트롤러
 *
 * 1.02
 * 계획 확인용 수도코드 작성
 *
 * 1.03
 * 리턴값을 ResponseEntity로 수정
 *
 * 1.04
 * api 및 pathVariable 수정
 * id -> roomId
 *
 * 1.05
 * chatRoomList 메서드 매개변수 간소화
 *
 * 1.06
 * Slf4j 패턴 추가
 * 리퀘스트 매핑 기본경로 수정 (private 추가)
 *
 * @author jhj010311@gmail.com
 * @version 1.06
 * @since 2025-10-20
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/chat/private")
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
        └─ POST   /api/chat/{roomId}/complete           구매 확정 등으로 인한 채팅방 종료
    * */

    // TODO: 프론트 페이지와 직결되는 각종 REST API를 작성하기
    // note: 리턴값은 dto.common.ApiResponse로 통일

    @GetMapping
    public ResponseEntity<ApiResponse<ChatRoomPageResponse>> chatRoomList(ChatRoomPageRequest request,
                                                                          @RequestHeader("X-User-Id") Long userId) {
        // 로그인한 유저의 id를 사용하여 유저가 참가한 적 있는 모든 채팅방 리스트를 쿼리
        // 현재 공동구매에 참가한 인원 정보도 넣어줘야 함

        log.info("채팅방 목록 요청 - request: {}, userId: {}", request, userId);

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
        // 채팅방 id를 사용하여 참가중인 특정 채팅방 페이지를 오픈

        log.info("채팅방 페이지 오픈 요청 - roomId: {}, userId: {}", chatRoomId, userId);

        if(chatRoomService.isParticipating(chatRoomId, userId)){
            return ResponseEntity.ok(ApiResponse.success(chatRoomService.getChatRoomDetails(chatRoomId)));
        }

        return ResponseEntity.badRequest().body(ApiResponse.fail("잘못된 요청입니다"));
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ApiResponse<ChatMessagePageResponse>> getMessages(ChatMessagePageRequest request,
                                                            @RequestHeader("X-User-Id") Long userId) {
        log.info("채팅 로그 요청 - request: {}, userId: {}", request, userId);

        ChatMessagePageResponse chatMessagePageResponse;

        if(request.getCursor() == null){
            chatMessagePageResponse = chatMessageService.getRecentMessages(request.getRoomId(), userId, request.getSize());
        } else {
            chatMessagePageResponse = chatMessageService.getMessagesBeforeCursor(request.getRoomId(), userId, request.getCursor(), request.getSize());
        }

        if(chatMessagePageResponse == null){
            return ResponseEntity.badRequest().body(ApiResponse.fail("잘못된 요청입니다"));
        }

        return ResponseEntity.ok(ApiResponse.success(chatMessagePageResponse));
    }

    @GetMapping("/{roomId}/participants")
    public ResponseEntity<ApiResponse<ParticipantListResponse>> getParticipants(@PathVariable("roomId") Long roomId) {
        // 참가자 목록 정보 쿼리
        log.info("채팅방 참가자 목록 요청 - roomId: {}", roomId);

        return ResponseEntity.ok(ApiResponse.success(chatRoomService.getParticipants(roomId)));
    }

    @PostMapping("/{roomId}/exit")
    public ResponseEntity<ApiResponse<String>> leaveChatRoom(@PathVariable("roomId") Long roomId,
                                                             @RequestHeader("X-User-Id") Long userId) {
        log.info("채팅방 탈퇴 요청 - roomId: {}, userId: {}", roomId, userId);

        // 1. 채팅방 참가자인지 검증
        if(!chatRoomService.isParticipating(roomId, userId)){
            return ResponseEntity.badRequest().body(ApiResponse.fail("참가 중인 채팅방이 아닙니다"));
        }

        // 2. 방장은 퇴장 불가
        if(chatRoomService.isCreator(roomId, userId)) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("방장은 채팅방을 닫기 전에 퇴장할 수 없습니다"));
        }

        // 3. 채팅방 탈퇴
        String response = chatRoomService.leaveChatRoom(roomId, userId);

        // TODO: STOMP를 통한 시스템 메시지 발송 필요
        // stompService.sendSystemMessage(roomId, userId + "님이 퇴장했습니다");

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{roomId}/kick")
    public ResponseEntity<ApiResponse<String>> kickParticipant(@PathVariable("roomId") Long roomId,
                                                               @RequestParam Long targetUserId,
                                                               @RequestHeader("X-User-Id") Long requesterId) {
        log.info("참가자 강퇴 요청 - roomId: {}, targetUserId: {}, requesterId: {}", roomId, targetUserId, requesterId);

        // 1. 방장 권한 인증
        if(!chatRoomService.isCreator(roomId, requesterId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail("방장만 강퇴할 수 있습니다"));
        }

        // 2. 대상이 참가자인지 확인
        if(!chatRoomService.isParticipating(roomId, targetUserId)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("해당 사용자는 채팅방 참가자가 아닙니다"));
        }

        // 3. 자기 자신은 강퇴 불가
        if(requesterId.equals(targetUserId)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("자기 자신은 강퇴할 수 없습니다"));
        }

        // 4. 강퇴 처리
        String result = chatRoomService.kickParticipant(roomId, targetUserId);

        // TODO: STOMP를 통한 강퇴 알림
        // stompService.notifyKicked(roomId, targetUserId);
        // stompService.sendSystemMessage(roomId, targetUserId + "님이 강퇴되었습니다");

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{roomId}/participate")
    public ResponseEntity<ApiResponse<BuyerConfirmResponse>> confirmBuyer(@PathVariable("roomId") Long roomId,
                                                                          @RequestHeader("X-User-Id") Long userId) {
        log.info("공동구매 참가 요청 - roomId: {}, userId: {}", roomId, userId);

        // 1. 채팅방 참가자인지 검증
        if(!chatRoomService.isParticipating(roomId, userId)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("참가 중인 채팅방이 아닙니다"));
        }

        // 2. 이미 구매자인지 확인
        if(chatRoomService.isBuyer(roomId, userId)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("이미 공동구매에 참가 중입니다"));
        }

        // 3. 공동구매 참가 처리
        BuyerConfirmResponse response = chatRoomService.confirmBuyer(roomId, userId);

        // TODO: STOMP를 통한 알림
        // stompService.sendSystemMessage(roomId, userId + "님이 공동구매에 참가했습니다");

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{roomId}/participate")
    public ResponseEntity<ApiResponse<BuyerConfirmResponse>> cancelBuyer(@PathVariable("roomId") Long roomId,
                                                                         @RequestHeader("X-User-Id") Long userId) {
        log.info("공동구매 참가 취소 요청 - roomId: {}, userId: {}", roomId, userId);

        // 1. 채팅방 참가자인지 검증
        if(!chatRoomService.isParticipating(roomId, userId)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("참가 중인 채팅방이 아닙니다"));
        }

        // 2. 구매자인지 확인
        if(!chatRoomService.isBuyer(roomId, userId)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("공동구매에 참가하지 않은 상태입니다"));
        }

        // 3. 공동구매 참가 취소
        BuyerConfirmResponse response = chatRoomService.cancelBuyer(roomId, userId);

        // TODO: STOMP를 통한 알림
        // stompService.sendSystemMessage(roomId, userId + "님이 공동구매 참가를 취소했습니다");

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{roomId}/extend")
    public ResponseEntity<ApiResponse<ExtendDeadlineResponse>> extendDeadline(@PathVariable("roomId") Long roomId,
                                                                              @RequestParam Integer hours,
                                                                              @RequestHeader("X-User-Id") Long userId) {
        log.info("공동구매 기한 연장 요청 - roomId: {}, hours: {}, userId: {}", roomId, hours, userId);

        // 1. 방장 검증
        if(!chatRoomService.isCreator(roomId, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail("방장만 기한을 연장할 수 있습니다"));
        }

        // 2. 유효한 시간인지 검증
        if(hours <= 0 || hours > 72) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail("연장 시간은 1시간 이상 72시간 이하여야 합니다"));
        }

        // 3. 기한 연장 처리
        ExtendDeadlineResponse response = chatRoomService.extendDeadline(roomId, hours);

        // TODO: STOMP를 통한 시스템 메시지
        // stompService.sendSystemMessage(roomId, "모집 기한이 " + hours + "시간 연장되었습니다");

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{roomId}/close")
    public ResponseEntity<ApiResponse<RecruitmentCloseResponse>> closeRecruitment(@PathVariable("roomId") Long roomId,
                                                                                  @RequestHeader("X-User-Id") Long userId) {
        log.info("공동구매 모집마감 요청 - roomId: {}, userId: {}", roomId, userId);

        // 1. 방장 검증
        if(!chatRoomService.isCreator(roomId, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail("방장만 모집을 마감할 수 있습니다"));
        }

        // 2. 모집 마감 처리
        RecruitmentCloseResponse response = chatRoomService.closeRecruitment(roomId);

        // TODO: STOMP를 통한 시스템 메시지
        // stompService.sendSystemMessage(roomId, "공동구매 모집이 마감되었습니다");
        // 구매자가 아닌 참가자들에게 퇴장 알림 필요

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{roomId}/complete")
    public ResponseEntity<ApiResponse<String>> completePurchase(@PathVariable("roomId") Long roomId,
                                                                @RequestHeader("X-User-Id") Long userId) {
        log.info("공동구매 채팅방 종료 요청 - roomId: {}, userId: {}", roomId, userId);

        // 1. 방장 검증
        if(!chatRoomService.isCreator(roomId, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.fail("방장만 채팅방을 종료할 수 있습니다"));
        }

        // 2. 채팅방 종료 처리
        String result = chatRoomService.completePurchase(roomId);

        // TODO: STOMP를 통한 종료 알림
        // stompService.sendSystemMessage(roomId, "공동구매가 종료되었습니다");
        // 모든 참가자에게 채팅방 종료 알림

        // TODO: 스케줄러 등록
        // 모집 마감으로부터 일정 시간 후 자동 종료를 위한 스케줄러 작업 필요

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}