package com.bytogether.chatservice.repository;

import com.bytogether.chatservice.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅 메시지 엔티티에 대한 데이터베이스 접근을 담당하는 레포지토리
 *
 * v1.01 : 커서 기반 페이지네이션에 필요한 메서드 삽입
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-08
 */

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 커서 기반 페이지네이션 - 최초 로드
     * 특정 채팅방의 최근 메시지를 N개 조회 (삭제되지 않은 메시지만)
     */
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE m.chatRoom.id = :chatRoomId " +
            "AND m.isDeleted = false " +
            "ORDER BY m.sentAt DESC, m.id DESC")
    List<ChatMessage> findRecentMessages(
            @Param("chatRoomId") Long chatRoomId,
            Pageable pageable
    );

    /**
     * 커서 기반 페이지네이션 - 이전 메시지 로드
     * 특정 커서(메시지 ID) 이전의 메시지를 N개 조회
     */
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE m.chatRoom.id = :chatRoomId " +
            "AND m.id < :cursorId " +
            "AND m.isDeleted = false " +
            "ORDER BY m.sentAt DESC, m.id DESC")
    List<ChatMessage> findMessagesBeforeCursor(
            @Param("chatRoomId") Long chatRoomId,
            @Param("cursorId") Long cursorId,
            Pageable pageable);

    /**
     * 특정 시간 범위 내 메시지 조회 (참가자 이력 기반 열람 권한 체크용)
     */
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE m.chatRoom.id = :chatRoomId " +
            "AND m.isDeleted = false " +
            "AND m.sentAt >= :viewableFrom " +
            "AND (:viewableUntil IS NULL OR m.sentAt <= :viewableUntil) " +
            "ORDER BY m.sentAt DESC, m.id DESC")
    List<ChatMessage> findViewableMessages(
            @Param("chatRoomId") Long chatRoomId,
            @Param("viewableFrom") LocalDateTime viewableFrom,
            @Param("viewableUntil") LocalDateTime viewableUntil,
            Pageable pageable);

    /**
     * 특정 시간 범위 + 커서 기반 메시지 조회
     */
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE m.chatRoom.id = :chatRoomId " +
            "AND m.id < :cursorId " +
            "AND m.isDeleted = false " +
            "AND m.sentAt >= :viewableFrom " +
            "AND (:viewableUntil IS NULL OR m.sentAt <= :viewableUntil) " +
            "ORDER BY m.sentAt DESC, m.id DESC")
    List<ChatMessage> findViewableMessagesBeforeCursor(
            @Param("chatRoomId") Long chatRoomId,
            @Param("cursorId") Long cursorId,
            @Param("viewableFrom") LocalDateTime viewableFrom,
            @Param("viewableUntil") LocalDateTime viewableUntil,
            Pageable pageable);


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