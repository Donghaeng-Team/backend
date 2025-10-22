package com.bytogether.chatservice.service;


import com.bytogether.chatservice.client.UserServiceClient;
import com.bytogether.chatservice.client.dto.UserInfoRequest;
import com.bytogether.chatservice.client.dto.UserInternalResponse;
import com.bytogether.chatservice.config.RedisPublish;
import com.bytogether.chatservice.dto.request.ChatMessageSendRequest;
import com.bytogether.chatservice.dto.response.ChatMessagePageResponse;
import com.bytogether.chatservice.dto.response.ChatMessageResponse;
import com.bytogether.chatservice.entity.*;
import com.bytogether.chatservice.mapper.ChatMessageMapper;
import com.bytogether.chatservice.repository.ChatMessageRepository;
import com.bytogether.chatservice.repository.ChatRoomParticipantHistoryRepository;
import com.bytogether.chatservice.repository.ChatRoomParticipantRepository;
import com.bytogether.chatservice.repository.ChatRoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final ChatRoomParticipantHistoryRepository historyRepository;
    private final ChatMessageMapper messageMapper;
    private final EntityManager em;
    private final ChatMessageMapper chatMessageMapper;
    // private final UserService userService; // 닉네임 조회용 (MSA 환경에서는 FeignClient 등)

    //region ============= 현재 세션 기반 메시지 획득 (기존 방식) =============
    /**
     * 채팅방 최초 접속 시 최근 메시지 조회
     */
    public ChatMessagePageResponse getRecentMessages(Long chatRoomId, Long userId) {
        return getRecentMessages(chatRoomId, userId, DEFAULT_PAGE_SIZE);
    }

    /**
     * 채팅방 최초 접속 시 최근 메시지 조회
     */
    public ChatMessagePageResponse getRecentMessages(Long chatRoomId, Long userId, int size) {
        // 참가자 정보 조회
        ChatRoomParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElse(null);

        if (participant == null || participant.getStatus() != ParticipantStatus.ACTIVE) {
            // 참가자가 아니거나 비활성 상태
            return null;
        }

        // N+1개 조회
        Pageable pageable = PageRequest.of(0, size + 1);

        List<ChatMessage> messages = messageRepository.findRecentMessages(
                chatRoomId,
                participant.getJoinedAt(),
                pageable
        );

        return buildChatMessagePageResponse(messages, size);
    }

    /**
     * 이전 메시지 조회 (무한 스크롤)
     */
    public ChatMessagePageResponse getMessagesBeforeCursor(Long chatRoomId, Long userId, Long cursorId, int size) {
        // 참가자 정보 조회
        ChatRoomParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
                .orElse(null);

        if (participant == null || participant.getStatus() != ParticipantStatus.ACTIVE) {
            // 참가자가 아니거나 비활성 상태
            return null;
        }

        Pageable pageable = PageRequest.of(0, size + 1);

        List<ChatMessage> messages = messageRepository.findMessagesBeforeCursor(
                chatRoomId,
                cursorId,
                participant.getJoinedAt(),
                pageable
        );

        return buildChatMessagePageResponse(messages, size);
    }

    /**
     * 현재 참가자의 열람 가능 이력 조회
     */
    private ChatRoomParticipantHistory getCurrentParticipantHistory(Long chatRoomId, Long userId) {
        return historyRepository.findTopByUserIdAndChatRoomIdAndLeftAtIsNull(userId, chatRoomId)
                .orElse(null);
    }
    //endregion

    //region ============= 전체 History 기반 (폐기) =============
//
//    /**
//     * 모든 참가 이력을 고려한 메시지 조회 (여러 구간 통합)
//     */
//    public ChatMessagePageResponse getAllViewableMessages(Long chatRoomId, Long userId, int size) {
//        // 해당 사용자의 모든 참가 이력 조회
//        List<ChatRoomParticipantHistory> allHistories = historyRepository
//                .findAllByUserIdAndChatRoomIdOrderByJoinedAtDesc(userId, chatRoomId);
//
//        if (allHistories.isEmpty()) {
//            return emptyResponse();
//        }
//
//        // 열람 가능한 시간 구간들을 추출
//        List<ViewablePeriod> viewablePeriods = extractViewablePeriods(allHistories);
//
//        // 복잡한 쿼리를 위해 Specification 또는 QueryDSL 사용
//        Pageable pageable = PageRequest.of(0, size + 1);
//        List<ChatMessage> messages = messageRepository.findMessagesInPeriods(
//                chatRoomId,
//                viewablePeriods,
//                pageable
//        );
//
//        return buildChatMessagePageResponse(messages, size);
//    }
//
//    /**
//     * 커서 기반 - 전체 History
//     */
//    public ChatMessagePageResponse getAllViewableMessagesBeforeCursor(
//            Long chatRoomId, Long userId, Long cursorId, int size) {
//
//        List<ChatRoomParticipantHistory> allHistories = historyRepository
//                .findAllByUserIdAndChatRoomIdOrderByJoinedAtDesc(userId, chatRoomId);
//
//        if (allHistories.isEmpty()) {
//            return emptyResponse();
//        }
//
//        List<ViewablePeriod> viewablePeriods = extractViewablePeriods(allHistories);
//
//        Pageable pageable = PageRequest.of(0, size + 1);
//        List<ChatMessage> messages = messageRepository.findMessagesInPeriodsBeforeCursor(
//                chatRoomId,
//                cursorId,
//                viewablePeriods,
//                pageable
//        );
//
//        return buildChatMessagePageResponse(messages, size);
//    }
//
//    // ============= Helper Methods =============
//
//    /**
//     * History 리스트에서 열람 가능 구간들 추출
//     */
//    private List<ViewablePeriod> extractViewablePeriods(List<ChatRoomParticipantHistory> histories) {
//        return histories.stream()
//                .filter(h -> h.getViewableFrom() != null)
//                .map(h -> new ViewablePeriod(
//                        h.getViewableFrom(),
//                        h.getViewableUntil() != null ? h.getViewableUntil() : LocalDateTime.now()
//                ))
//                .collect(Collectors.toList());
//    }
//
//    private ChatMessagePageResponse emptyResponse() {
//        return ChatMessagePageResponse.builder()
//                .messages(Collections.emptyList())
//                .hasMore(false)
//                .nextCursor(null)
//                .build();
//    }
    //endregion



    // stomp controller용

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserServiceClient userServiceClient;

    /**
     * 일반 메시지 전송
     * @RedisPublish - AOP가 자동으로 Redis 발행
     */
    @Transactional
    @RedisPublish  // ← AOP가 반환값을 Redis로 자동 발행
    public ChatMessageResponse sendMessage(Long roomId, Long senderUserId, ChatMessageSendRequest request) {
        // 1. 권한 검증
        validateActiveParticipant(roomId, senderUserId);

        // 2. 엔티티 생성
        ChatRoom chatRoom = em.getReference(ChatRoom.class, roomId);

        UserInfoRequest userInfoRequest = UserInfoRequest.buildRequest(senderUserId);
        UserInternalResponse senderInfo = userServiceClient.getUserInfo(userInfoRequest);

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderUserId(senderUserId)
                .senderNickname(senderInfo.getNickName())
                .senderProfileUrl(senderInfo.getImageUrl())
                .messageType(MessageType.TEXT)
                .messageContent(request.getMessageContent())
                .build();

        // 3. DB 저장
        ChatMessage saved = messageRepository.save(chatMessage);

        // 4. DTO 변환
        ChatMessageResponse response = chatMessageMapper.toResponse(saved);

        // 5. 현재 Pod의 클라이언트들에게 전송
        broadcastToCurrentPod(roomId, response);

        // 6. list_order_time 업데이트
        updateListOrderTime(roomId, saved.getSentAt());

        // 7. 반환 - AOP가 자동으로 Redis 발행
        return response;
    }

    /**
     * 채팅방 퇴장 처리 (일시 접속 해제)
     */
    @Transactional
    public void handleLeaveRoom(Long roomId, Long userId) {
        log.info("유저의 채팅방 연결 해제 - userId {} roomId {}", userId, roomId);
    }

    /**
     * 시스템 메시지 전송
     * @RedisPublish - AOP가 자동으로 Redis 발행
     */
    @Transactional
    @RedisPublish
    public ChatMessageResponse sendSystemMessage(Long roomId, String message) {
        ChatRoom chatRoom = em.getReference(ChatRoom.class, roomId);

        ChatMessage systemMessage = ChatMessage.systemMessage(chatRoom, message);

        ChatMessage saved = messageRepository.save(systemMessage);
        ChatMessageResponse response = chatMessageMapper.toResponse(saved);

        broadcastToCurrentPod(roomId, response);
        updateListOrderTime(roomId, saved.getSentAt());

        return response;  // AOP가 Redis로 자동 발행
    }

    /**
     * 강퇴당한 사용자에게 개인 알림
     */
    public void notifyKickedUser(Long userId, Long roomId, String reason) {
        String message = chatRoomRepository.findTitleById(roomId) + " 채팅방에서 강퇴되었습니다.";
        if (reason != null && !reason.isBlank()) {
            message += " 사유: " + reason;
        }

        // 현재 Pod
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                message
        );

        // 다른 Pod들 (Redis Pub/Sub)
        redisTemplate.convertAndSend("user:" + userId + ":kicked", message);
    }

    public void notifyUser(Long userId, Long roomId, String reason) {
        String message = chatRoomRepository.findTitleById(roomId) + " 채팅방에서 알림 : \n" + reason;

        // 현재 Pod
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                message
        );

        // 다른 Pod들 (Redis Pub/Sub)
        redisTemplate.convertAndSend("user:" + userId + ":kicked", message);
    }

    /**
     * 현재 Pod의 클라이언트들에게만 전송
     */
    private void broadcastToCurrentPod(Long roomId, ChatMessageResponse response) {
        messagingTemplate.convertAndSend(
                "/topic.rooms." + roomId + ".messages",
                response
        );
    }

    private void updateListOrderTime(Long roomId, LocalDateTime messageTime) {
        participantRepository.updateListOrderTimeForAllActiveParticipants(roomId, messageTime);
    }

    private void validateActiveParticipant(Long roomId, Long userId) {
        if (!participantRepository.existsByChatRoomIdAndUserIdAndStatus(
                roomId, userId, ParticipantStatus.ACTIVE)) {
            throw new ForbiddenException("채팅방에 참여 중이지 않습니다");
        }
    }

    private String getUserNickname(Long userId) {
        return "User#" + userId;
    }


    /**
     * ChatMessagePageResponse 생성
     */
    private ChatMessagePageResponse buildChatMessagePageResponse(List<ChatMessage> messages, int size) {
        // 다음 페이지 존재 여부 확인
        boolean hasMore = messages.size() > size;

        // N+1개 조회했으면 마지막 하나 제거
        List<ChatMessage> resultMessages = hasMore
                ? messages.subList(0, size)
                : messages;

        // 다음 커서는 가장 오래된 메시지의 ID (리스트의 마지막)
        Long nextCursor = resultMessages.isEmpty()
                ? null
                : resultMessages.get(resultMessages.size() - 1).getId();

        // 메시지를 시간 순서대로 정렬 (UI에서는 오래된 것부터 위에 표시)
        Collections.reverse(resultMessages);

        // DTO 변환
        List<ChatMessageResponse> messageResponses = resultMessages.stream()
                .map(this.messageMapper::toResponse)
                .collect(Collectors.toList());

        return ChatMessagePageResponse.builder()
                .messages(messageResponses)
                .hasMore(hasMore)
                .nextCursor(nextCursor)
                .build();
    }
}
