package com.bytogether.chatservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅방 페이지 스크롤 로딩용 dto
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-13
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListPageResponse {

    private List<ChatRoomResponse> chatRooms;
    private Boolean hasMore;  // 다음 페이지 존재 여부

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime nextCursor;  // 다음 페이지 커서 (listOrderTime)

    private Long nextParticipantId;  // 동일 시간대 구분용
}