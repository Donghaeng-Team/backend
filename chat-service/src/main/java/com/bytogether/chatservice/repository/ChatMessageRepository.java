package com.bytogether.chatservice.repository;

import com.bytogether.chatservice.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅 메시지 엔티티에 대한 데이터베이스 접근을 담당하는 레포지토리
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-07
 */

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomIdAndIsDeletedFalseOrderBySentAtDesc(Long chatRoomId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId " +
            "AND cm.isDeleted = false " +
            "AND cm.sentAt BETWEEN :startTime AND :endTime " +
            "ORDER BY cm.sentAt ASC")
    List<ChatMessage> findMessagesBetween(@Param("chatRoomId") Long chatRoomId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    long countByChatRoomIdAndIsDeletedFalse(Long chatRoomId);
}