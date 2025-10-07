package com.bytogether.chatservice.repository;

import com.bytogether.chatservice.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 참가자 엔티티에 대한 데이터베이스 접근을 담당하는 레포지토리
 *
 * @author jhj010311@gmail.com
 * @version 1.0
 * @since 2025-10-07
 */

@Repository
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    Optional<ChatRoomParticipant> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    List<ChatRoomParticipant> findByChatRoomIdAndStatus(Long chatRoomId, ParticipantStatus status);

    List<ChatRoomParticipant> findByChatRoomIdAndIsBuyerFalse(Long chatRoomId);

    @Query("SELECT COUNT(p) FROM ChatRoomParticipant p " +
            "WHERE p.chatRoom.id = :chatRoomId AND p.status = 'ACTIVE'")
    long countActiveParticipants(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT COUNT(p) FROM ChatRoomParticipant p " +
            "WHERE p.chatRoom.id = :chatRoomId AND p.isBuyer = true")
    long countBuyers(@Param("chatRoomId") Long chatRoomId);

    List<ChatRoomParticipant> findByUserIdAndStatus(Long userId, ParticipantStatus status);

    boolean existsByChatRoomIdAndUserIdAndIsPermanentlyBannedTrue(Long chatRoomId, Long userId);
}