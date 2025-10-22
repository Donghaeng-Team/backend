package com.bytogether.chatservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 채팅방 참가자 내역에 대한 정보를 담는 엔티티
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-07
 */

@Entity
@Table(name = "chat_room_participant_history", indexes = {
        @Index(name = "idx_participant_history_user",
                columnList = "user_id, chat_room_id"),
        @Index(name = "idx_participant_history_room",
                columnList = "chat_room_id"),
        @Index(name = "idx_participant_history_viewable",
                columnList = "chat_room_id, user_id, viewable_from, viewable_until")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomParticipantHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 입/퇴장 이력
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "exit_type", length = 30)
    private ExitType exitType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}