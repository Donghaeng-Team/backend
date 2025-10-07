package com.bytogether.chatservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 채팅방 참가자에 대한 정보를 담는 엔티티
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-07
 */

@Entity
@Table(name = "chat_room_participant",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_participant_room_user",
                        columnNames = {"chat_room_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_participant_room_status",
                        columnList = "chat_room_id, status"),
                @Index(name = "idx_participant_user",
                        columnList = "user_id, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 참가 정보
    @CreationTimestamp
    @Column(name = "first_joined_at", nullable = false)
    private LocalDateTime firstJoinedAt;

    @Column(name = "last_left_at")
    private LocalDateTime lastLeftAt;

    // 공동구매 참여 여부
    @Column(name = "is_buyer")
    @Builder.Default
    private Boolean isBuyer = false;

    @Column(name = "buyer_confirmed_at")
    private LocalDateTime buyerConfirmedAt;

    // 현재 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.ACTIVE;

    @Column(name = "is_permanently_banned")
    @Builder.Default
    private Boolean isPermanentlyBanned = false;

    // 타임스탬프
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 편의 메서드
    public void confirmBuyer() {
        this.isBuyer = true;
        this.buyerConfirmedAt = LocalDateTime.now();
    }

    public void leave(ParticipantStatus exitStatus) {
        this.status = exitStatus;
        this.lastLeftAt = LocalDateTime.now();
    }

    public void ban() {
        this.status = ParticipantStatus.BANNED;
        this.isPermanentlyBanned = true;
        this.lastLeftAt = LocalDateTime.now();
    }
}