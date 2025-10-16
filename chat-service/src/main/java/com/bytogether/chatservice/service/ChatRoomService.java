package com.bytogether.chatservice.service;

import com.bytogether.chatservice.dto.response.ChatRoomListPageResponse;
import com.bytogether.chatservice.dto.response.ChatRoomResponse;
import com.bytogether.chatservice.entity.ChatRoomParticipant;
import com.bytogether.chatservice.entity.ChatRoomParticipantHistory;
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
    public ChatRoomListPageResponse getMyChatRooms(Long userId, int size) {
        // 활성 + 퇴장한 채팅방 모두 조회
        List<ParticipantStatus> statuses = List.of(
                ParticipantStatus.ACTIVE,
                ParticipantStatus.LEFT_NOT_BUYER,
                ParticipantStatus.LEFT_COMPLETED
        );

        // N+1개 조회하여 다음 페이지 존재 여부 확인
        Pageable pageable = PageRequest.of(0, size + 1);

        List<ChatRoomParticipant> participants = participantRepository
                .findMyRecentChatRooms(userId, statuses, pageable);

        return chatRoomMapper.buildPageResponse(participants, size);
    }

    /**
     * 내 채팅방 목록 조회 - 커서 기반 (다음 페이지)
     */
    public ChatRoomListPageResponse getMyChatRooms(
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

        List<ChatRoomParticipant> participants = participantRepository
                .findMyChatRoomsBeforeCursor(userId, statuses, cursor, participantId, pageable);

        return chatRoomMapper.buildPageResponse(participants, size);
    }

    public boolean enterChatRoom(Long roomId, Long userId) {
        // 채팅방에 참가한 상태인 유저가 맞는지 확인

        return participantRepository.existsByChatRoomIdAndUserIdAndStatus(roomId, userId, ParticipantStatus.ACTIVE);
    }
}
