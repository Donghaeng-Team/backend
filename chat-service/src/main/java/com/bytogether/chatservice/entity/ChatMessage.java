package com.bytogether.chatservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 채팅 메세지에 대한 정보를 담는 엔티티
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-07
 */

@Entity
@Table(name = "chat_message", indexes = {
        @Index(name = "idx_chat_message_room", columnList = "chat_room_id, sent_at"),
        @Index(name = "idx_chat_message_sender", columnList = "sender_user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "sender_user_id", nullable = true)
    private Long senderUserId;

    @Column(name = "sender_nickname", nullable = false)
    private String senderNickname;

    @Column(name = "sender_profile_url", nullable = true)
    private String senderProfileUrl;

    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 20)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    // 소프트 삭제
    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 편의 메서드
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // 시스템 메시지 생성 팩토리 메서드
    public static ChatMessage systemMessage(ChatRoom room, String content) {
        return ChatMessage.builder()
                .chatRoom(room)
                .senderUserId(null)  // NULL로 설정
                .senderNickname("system")
                .senderProfileUrl(null)
                .messageType(MessageType.SYSTEM)
                .messageContent(content)
                .build();
    }

    public static ChatMessage extendMessage(ChatRoom room, String content) {
        return ChatMessage.builder()
                .chatRoom(room)
                .senderUserId(null)  // NULL로 설정
                .senderNickname("system")
                .senderProfileUrl(null)
                .messageType(MessageType.DEADLINE_EXTEND)
                .messageContent(content)
                .build();
    }
}