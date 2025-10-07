package com.bytogether.chatservice.repository;

import com.bytogether.chatservice.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 채팅방 참가자 내역 엔티티에 대한 데이터베이스 접근을 담당하는 레포지토리
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-07
 */

@Repository
public interface ChatRoomParticipantHistoryRepository extends JpaRepository<ChatRoomParticipantHistory, Long> {

    List<ChatRoomParticipantHistory> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);

    Optional<ChatRoomParticipantHistory> findTopByUserIdAndChatRoomIdAndLeftAtIsNull(
            Long userId, Long chatRoomId);

    @Query("SELECT h FROM ChatRoomParticipantHistory h " +
            "WHERE h.chatRoom.id = :chatRoomId AND h.userId = :userId " +
            "AND h.viewableFrom IS NOT NULL " +
            "AND (h.viewableFrom <= :messageTime) " +
            "AND (h.viewableUntil IS NULL OR h.viewableUntil >= :messageTime)")
    Optional<ChatRoomParticipantHistory> findViewableHistoryForMessage(
            @Param("chatRoomId") Long chatRoomId,
            @Param("userId") Long userId,
            @Param("messageTime") LocalDateTime messageTime);
}