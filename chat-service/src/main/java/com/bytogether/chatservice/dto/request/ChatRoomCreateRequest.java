package com.bytogether.chatservice.dto.request;

import com.bytogether.chatservice.entity.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 채팅방 개설을 요청하는 dto
 * market-service가 이용함
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-20
 */

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomCreateRequest {
    private Long marketId;
    private Long creatorUserId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    private Integer minBuyers;
    private Integer maxBuyers;
    private String thumbnailUrl;
    private String title;


    public ChatRoom toChatRoom() {
        return ChatRoom.builder()
                .marketId(this.getMarketId())
                .creatorUserId(this.getCreatorUserId())
                .endTime(this.getEndTime())
                .minBuyers(this.getMinBuyers())
                .maxBuyers(this.getMaxBuyers())
                .thumbnailUrl(this.getThumbnailUrl())
                .title(this.getTitle())
                .build();
    }
}
