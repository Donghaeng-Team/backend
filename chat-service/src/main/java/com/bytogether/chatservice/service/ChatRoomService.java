package com.bytogether.chatservice.service;

import com.bytogether.chatservice.dto.response.ChatRoomPageResponse;
import com.bytogether.chatservice.dto.response.ChatRoomResponse;
import com.bytogether.chatservice.entity.ChatRoomParticipant;
import com.bytogether.chatservice.entity.ParticipantStatus;
import com.bytogether.chatservice.mapper.ChatRoomMapper;
import com.bytogether.chatservice.repository.ChatRoomParticipantHistoryRepository;
import com.bytogether.chatservice.repository.ChatRoomParticipantRepository;
import com.bytogether.chatservice.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 채팅방 목록 담당 서비스
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-13
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {
    ChatRoomRepository chatRoomRepository;
    ChatRoomParticipantRepository participantRepository;
    ChatRoomParticipantHistoryRepository historyRepository;
    ChatRoomMapper chatRoomMapper;

    /**
     * 내 채팅방 목록 조회 - 최초 로드 (페이지 크기 지정)
     */
    public ChatRoomPageResponse getMyChatRooms(Long userId, int size) {
        // 활성 + 퇴장한 채팅방 모두 조회
        List<ParticipantStatus> statuses = List.of(
                ParticipantStatus.ACTIVE,
                ParticipantStatus.LEFT_NOT_BUYER,
                ParticipantStatus.LEFT_COMPLETED
        );

        // N+1개 조회하여 다음 페이지 존재 여부 확인
        Pageable pageable = PageRequest.of(0, size + 1);

        List<ChatRoomParticipant> userParticipations  = participantRepository
                .findMyRecentChatRooms(userId, statuses, pageable);

        // 참여한 채팅방의 id들 추출
        List<Long> roomIds = userParticipations.stream()
                .map(p -> p.getChatRoom().getId())
                .collect(Collectors.toList());

        // 추출한 채팅방 id로 공동구매 참가자 수 획득
        Map<Long, Integer> buyerCounts = participantRepository.countBuyersByRoomIds(roomIds);

        return chatRoomMapper.buildPageResponse(userParticipations, buyerCounts, size);
    }

    /**
     * 내 채팅방 목록 조회 - 커서 기반 (다음 페이지)
     */
    public ChatRoomPageResponse getMyChatRooms(
            Long userId,
            LocalDateTime cursor,
            Long participantId,
            int size) {

        List<ParticipantStatus> statuses = List.of(
                ParticipantStatus.ACTIVE,
                ParticipantStatus.LEFT_NOT_BUYER,
                ParticipantStatus.LEFT_COMPLETED
        );

        Pageable pageable = PageRequest.of(0, size + 1);

        List<ChatRoomParticipant> userParticipations = participantRepository
                .findMyChatRoomsBeforeCursor(userId, statuses, cursor, participantId, pageable);

        // 참여한 채팅방의 id들 추출
        List<Long> roomIds = userParticipations.stream()
                .map(p -> p.getChatRoom().getId())
                .collect(Collectors.toList());

        // 추출한 채팅방 id로 공동구매 참가자 수 획득
        Map<Long, Integer> buyerCounts = participantRepository.countBuyersByRoomIds(roomIds);

        return chatRoomMapper.buildPageResponse(userParticipations, buyerCounts, size);
    }

    public boolean isParticipating(Long roomId, Long userId) {
        // 채팅방에 참가한 상태인 유저가 맞는지 확인

        return participantRepository.existsByChatRoomIdAndUserIdAndStatus(roomId, userId, ParticipantStatus.ACTIVE);
    }

    public ChatRoomResponse getChatRoomDetails(Long chatRoomId) {
        return chatRoomRepository.getChatRoomById(chatRoomId);
    }
}
