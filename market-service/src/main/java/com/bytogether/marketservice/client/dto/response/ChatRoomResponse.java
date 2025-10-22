package com.bytogether.marketservice.client.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ChatRoomResponse {
    private Long id;
    private Long creatorUserId;
    private Long marketId;
    private String title;
    private String thumbnailUrl;
    private Integer minBuyers;
    private Integer maxBuyers;
    private Integer currentBuyers;
    private Integer currentParticipants;
    private ChatRoomStatus status;
    private ParticipantStatus participantStatus;
    private boolean isBuyer;
    private boolean isCreator;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastMessageAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime listOrderTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recruitmentClosedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
