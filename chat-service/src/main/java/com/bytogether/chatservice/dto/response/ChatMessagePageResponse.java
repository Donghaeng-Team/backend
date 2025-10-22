package com.bytogether.chatservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ChatMessagePageResponse {
    private List<ChatMessageResponse> messages;
    private boolean hasMore;           // 더 불러올 메시지가 있는지
    private Long nextCursor;           // 다음 페이지 조회용 커서 (가장 오래된 메시지 ID)
}
