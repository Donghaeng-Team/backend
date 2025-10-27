package com.bytogether.chatservice.mapper;

import com.bytogether.chatservice.entity.ChatMessage;
import com.bytogether.chatservice.dto.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatMessageMapper {

    // private final UserServiceClient userServiceClient; // 나중에 추가

    /**
     * Entity -> DTO 변환 (단일)
     */
    public ChatMessageResponse toResponse(ChatMessage message) {

        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderUserId())
                .senderNickname(message.getSenderNickname())
                .messageContent(message.getMessageContent())
                .messageType(message.getMessageType())
                .sentAt(message.getSentAt())
                .build();
    }

    /**
     * Entity List -> DTO List 변환 (일괄)
     * N+1 문제 방지를 위해 UserService 일괄 조회
     */
    public List<ChatMessageResponse> toResponseList(List<ChatMessage> messages) {
        if (messages.isEmpty()) {
            return List.of();
        }

        // 1. 메시지에서 모든 고유한 senderId 추출
        List<Long> senderIds = messages.stream()
                .map(ChatMessage::getSenderUserId)
                .distinct()
                .collect(Collectors.toList());

        // 2. UserService에서 일괄 조회 (1번의 API 호출)
        Map<Long, String> nicknameMap = fetchNicknamesInBatch(senderIds);

        // 3. 변환
        return messages.stream()
                .map(message -> toResponse(message, nicknameMap))
                .collect(Collectors.toList());
    }

    /**
     * 닉네임 맵을 사용한 변환 (일괄 조회 후)
     */
    private ChatMessageResponse toResponse(ChatMessage message, Map<Long, String> nicknameMap) {
        String nickname = nicknameMap.getOrDefault(
                message.getSenderUserId(),
                "User#" + message.getSenderUserId()
        );

        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderUserId())
                .senderNickname(nickname)
                .messageContent(message.getMessageContent())
                .messageType(message.getMessageType())
                .sentAt(message.getSentAt())
                .build();
    }

    /**
     * UserService에서 닉네임 일괄 조회
     */
    private Map<Long, String> fetchNicknamesInBatch(List<Long> userIds) {
        // TODO: 실제 UserService 호출
        // return userServiceClient.getNicknamesByUserIds(userIds);

        // 임시 구현
        return userIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        userId -> "User#" + userId
                ));
    }
}