package com.bytogether.chatservice.service;


import com.bytogether.chatservice.dto.common.ViewablePeriod;
import com.bytogether.chatservice.dto.response.ChatMessagePageResponse;
import com.bytogether.chatservice.dto.response.ChatMessageResponse;
import com.bytogether.chatservice.entity.ChatMessage;
import com.bytogether.chatservice.entity.ChatRoomParticipantHistory;
import com.bytogether.chatservice.mapper.ChatMessageMapper;
import com.bytogether.chatservice.repository.ChatMessageRepository;
import com.bytogether.chatservice.repository.ChatRoomParticipantHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private final ChatMessageRepository messageRepository;
    private final ChatRoomParticipantHistoryRepository historyRepository;
    private final ChatMessageMapper messageMapper;
    // private final UserService userService; // 닉네임 조회용 (MSA 환경에서는 FeignClient 등)

    //region ============= 현재 세션 기반 메시지 획득 (기존 방식) =============
    /**
     * 채팅방 최초 접속 시 최근 메시지 조회
     */
    public ChatMessagePageResponse getRecentMessages(Long chatRoomId, Long userId) {
        return getRecentMessages(chatRoomId, userId, DEFAULT_PAGE_SIZE);
    }

    /**
     * 채팅방 최초 접속 시 최근 메시지 조회 (페이지 크기 지정)
     */
    public ChatMessagePageResponse getRecentMessages(Long chatRoomId, Long userId, int size) {
        // 열람 가능한 시간 범위 조회
        ChatRoomParticipantHistory currentHistory = getCurrentParticipantHistory(chatRoomId, userId);

        // N+1개 조회하여 다음 페이지 존재 여부 확인
        Pageable pageable = PageRequest.of(0, size + 1);

        List<ChatMessage> messages;
        if (currentHistory != null && currentHistory.getViewableFrom() != null) {
            // 열람 권한이 있는 경우 - 시간 범위 필터링
            messages = messageRepository.findViewableMessages(
                    chatRoomId,
                    currentHistory.getViewableFrom(),
                    currentHistory.getViewableUntil(),
                    pageable
            );
        } else {
            // 열람 권한이 없는 경우 - 빈 리스트 반환
            return ChatMessagePageResponse.builder()
                    .messages(Collections.emptyList())
                    .hasMore(false)
                    .nextCursor(null)
                    .build();
        }

        return buildChatMessagePageResponse(messages, size);
    }

    /**
     * 이전 메시지 조회 (무한 스크롤)
     */
    public ChatMessagePageResponse getMessagesBeforeCursor(Long chatRoomId, Long userId, Long cursorId, int size) {
        // 열람 가능한 시간 범위 조회
        ChatRoomParticipantHistory currentHistory = getCurrentParticipantHistory(chatRoomId, userId);

        // N+1개 조회
        Pageable pageable = PageRequest.of(0, size + 1);

        List<ChatMessage> messages;
        if (currentHistory != null && currentHistory.getViewableFrom() != null) {
            messages = messageRepository.findViewableMessagesBeforeCursor(
                    chatRoomId,
                    cursorId,
                    currentHistory.getViewableFrom(),
                    currentHistory.getViewableUntil(),
                    pageable
            );
        } else {
            return ChatMessagePageResponse.builder()
                    .messages(Collections.emptyList())
                    .hasMore(false)
                    .nextCursor(null)
                    .build();
        }

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

    //region ============= 전체 History 기반 (새로운 방식) =============

    /**
     * 모든 참가 이력을 고려한 메시지 조회 (여러 구간 통합)
     */
    public ChatMessagePageResponse getAllViewableMessages(Long chatRoomId, Long userId, int size) {
        // 해당 사용자의 모든 참가 이력 조회
        List<ChatRoomParticipantHistory> allHistories = historyRepository
                .findAllByUserIdAndChatRoomIdOrderByJoinedAtDesc(userId, chatRoomId);

        if (allHistories.isEmpty()) {
            return emptyResponse();
        }

        // 열람 가능한 시간 구간들을 추출
        List<ViewablePeriod> viewablePeriods = extractViewablePeriods(allHistories);

        // 복잡한 쿼리를 위해 Specification 또는 QueryDSL 사용
        Pageable pageable = PageRequest.of(0, size + 1);
        List<ChatMessage> messages = messageRepository.findMessagesInPeriods(
                chatRoomId,
                viewablePeriods,
                pageable
        );

        return buildChatMessagePageResponse(messages, size);
    }

    /**
     * 커서 기반 - 전체 History
     */
    public ChatMessagePageResponse getAllViewableMessagesBeforeCursor(
            Long chatRoomId, Long userId, Long cursorId, int size) {

        List<ChatRoomParticipantHistory> allHistories = historyRepository
                .findAllByUserIdAndChatRoomIdOrderByJoinedAtDesc(userId, chatRoomId);

        if (allHistories.isEmpty()) {
            return emptyResponse();
        }

        List<ViewablePeriod> viewablePeriods = extractViewablePeriods(allHistories);

        Pageable pageable = PageRequest.of(0, size + 1);
        List<ChatMessage> messages = messageRepository.findMessagesInPeriodsBeforeCursor(
                chatRoomId,
                cursorId,
                viewablePeriods,
                pageable
        );

        return buildChatMessagePageResponse(messages, size);
    }

    // ============= Helper Methods =============

    /**
     * History 리스트에서 열람 가능 구간들 추출
     */
    private List<ViewablePeriod> extractViewablePeriods(List<ChatRoomParticipantHistory> histories) {
        return histories.stream()
                .filter(h -> h.getViewableFrom() != null)
                .map(h -> new ViewablePeriod(
                        h.getViewableFrom(),
                        h.getViewableUntil() != null ? h.getViewableUntil() : LocalDateTime.now()
                ))
                .collect(Collectors.toList());
    }

    private ChatMessagePageResponse emptyResponse() {
        return ChatMessagePageResponse.builder()
                .messages(Collections.emptyList())
                .hasMore(false)
                .nextCursor(null)
                .build();
    }
    //endregion

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
