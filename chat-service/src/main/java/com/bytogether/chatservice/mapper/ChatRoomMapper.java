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
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-13
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
        Integer currentParticipants = buyerCounts.get(roomId);

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .thumbnailUrl(chatRoom.getThumbnailUrl())
                .maxBuyers(chatRoom.getMaxBuyers())
                .currentParticipants(currentParticipants)
                .status(chatRoom.getStatus())
                .listOrderTime(participant.getListOrderTime())
                .isBuyer(participant.getIsBuyer())                    // ← 구매 의사 확정 여부
                .participantStatus(participant.getStatus())           // ← 내 참가 상태
                .isCreator(chatRoom.getCreatorUserId()                // ← 방장 여부
                        .equals(participant.getUserId()))
                .build();
    }
}
