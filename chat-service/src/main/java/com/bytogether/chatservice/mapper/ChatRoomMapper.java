package com.bytogether.chatservice.mapper;

import com.bytogether.chatservice.dto.response.ChatRoomPageResponse;
import com.bytogether.chatservice.dto.response.ChatRoomResponse;
import com.bytogether.chatservice.entity.ChatRoom;
import com.bytogether.chatservice.entity.ChatRoomParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 채팅방 관련 클래스 변환용
 *
 * 1.01
 * 단일 채팅방용 변환 메서드 추가
 * ChatRoom 필드 변경 반영
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-17
 */

@Component
@RequiredArgsConstructor
public class ChatRoomMapper {

    /**
     * ChatRoomListPageResponse 생성
     */
    public ChatRoomPageResponse buildPageResponse(
            List<ChatRoomParticipant> userParticipations,
            Map<Long, Integer> buyerCounts, int size) {

        // 다음 페이지 존재 여부
        boolean hasMore = userParticipations.size() > size;

        // N+1개 조회했으면 마지막 하나 제거
        List<ChatRoomParticipant> resultParticipants = hasMore
                ? userParticipations.subList(0, size)
                : userParticipations;

        // 커서 정보 (마지막 userParticipations의 listOrderTime과 id)
        LocalDateTime nextCursor = null;
        Long nextParticipantId = null;

        if (!resultParticipants.isEmpty()) {
            ChatRoomParticipant lastParticipant = resultParticipants.get(resultParticipants.size() - 1);
            nextCursor = lastParticipant.getListOrderTime();
            nextParticipantId = lastParticipant.getId();
        }

        // DTO 변환
        List<ChatRoomResponse> chatRooms = resultParticipants.stream()
                .map(p -> convertToResponse(p, buyerCounts))
                .collect(Collectors.toList());

        return ChatRoomPageResponse.builder()
                .chatRooms(chatRooms)
                .hasMore(hasMore)
                .nextCursor(nextCursor)
                .nextParticipantId(nextParticipantId)
                .build();
    }

    /**
     * Participant -> DTO 변환
     */
    public ChatRoomResponse convertToResponse(ChatRoomParticipant participant, Map<Long, Integer> buyerCounts) {
        ChatRoom chatRoom = participant.getChatRoom();
        Long roomId = chatRoom.getId();
        Integer currentBuyers = buyerCounts.get(roomId);

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .thumbnailUrl(chatRoom.getThumbnailUrl())
                .minBuyers(chatRoom.getMinBuyers())
                .maxBuyers(chatRoom.getMaxBuyers())
                .currentBuyers(currentBuyers)
                .status(chatRoom.getStatus())
                .endTime(chatRoom.getEndTime())
                .listOrderTime(participant.getListOrderTime())
                .isBuyer(participant.getIsBuyer())                    // ← 구매 의사 확정 여부
                .participantStatus(participant.getStatus())           // ← 내 참가 상태
                .isCreator(chatRoom.getCreatorUserId()                // ← 방장 여부
                        .equals(participant.getUserId()))
                .build();
    }

    public ChatRoomResponse convertToResponse(ChatRoom room, Integer currentBuyers, Integer currentParticipants) {

        return ChatRoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .thumbnailUrl(room.getThumbnailUrl())
                .minBuyers(room.getMinBuyers())
                .maxBuyers(room.getMaxBuyers())
                .currentBuyers(currentBuyers)
                .currentParticipants(currentParticipants)
                .status(room.getStatus())
                .endTime(room.getEndTime())
                .lastMessageAt(room.getLastMessageAt())
                .recruitmentClosedAt(room.getRecruitmentClosedAt())
                .completedAt(room.getCompletedAt())
                .build();
    }

    public ChatRoomResponse convertToResponse(ChatRoom room) {

        return ChatRoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .thumbnailUrl(room.getThumbnailUrl())
                .minBuyers(room.getMinBuyers())
                .maxBuyers(room.getMaxBuyers())
                .status(room.getStatus())
                .endTime(room.getEndTime())
                .lastMessageAt(room.getLastMessageAt())
                .recruitmentClosedAt(room.getRecruitmentClosedAt())
                .completedAt(room.getCompletedAt())
                .build();
    }
}
