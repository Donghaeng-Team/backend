package com.bytogether.chatservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 채팅방에 대한 정보를 담는 엔티티
 *
 * v1.01
 * 시스템 메시지를 위한 테이블 변경사항 반영
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-15
 */

@Entity
@Table(name = "chat_room", indexes = {
        @Index(name = "idx_chat_room_market", columnList = "market_id"),
        @Index(name = "idx_chat_room_status", columnList = "status"),
        @Index(name = "idx_chat_room_creator", columnList = "creator_user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_user_id", nullable = false)
    private Long creatorUserId;

    @Column(name = "market_id", nullable = false)
    private Long marketId;


    // 모집 정보
    // endTime - 구인글에서 공시된 마감시각
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "max_buyers", nullable = false)
    private Integer maxBuyers;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "title", nullable = false, length = 255)
    private String title;


    // 상태 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ChatRoomStatus status = ChatRoomStatus.RECRUITING;

    // 실제로 공동구매할 모집이 완료된 시각
    @Column(name = "recruitment_closed_at")
    private LocalDateTime recruitmentClosedAt;

    // 상품 분배까지 끝내고 방장이 완전히 종료시킨 시각
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // 이 채팅방에서 마지막으로 채팅이 오간 시각
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    // 타임스탬프
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    // 연관 관계 (양방향 매핑 - 선택사항)
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatRoomParticipant> participants = new ArrayList<>();
}