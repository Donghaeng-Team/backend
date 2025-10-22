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
 * v1.01
 * 한 채팅방에 대해서 특정 유저의 모든 참가 이력을 쿼리하는 메서드 추가
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-16
 */

@Repository
public interface ChatRoomParticipantHistoryRepository extends JpaRepository<ChatRoomParticipantHistory, Long> {

    List<ChatRoomParticipantHistory> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);

    Optional<ChatRoomParticipantHistory> findTopByUserIdAndChatRoomIdAndLeftAtIsNull(
            Long userId, Long chatRoomId);

    List<ChatRoomParticipantHistory> findAllByUserIdAndChatRoomIdOrderByJoinedAtDesc(Long userId, Long chatRoomId);
}