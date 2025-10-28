package com.bytogether.chatservice.repository;

import com.bytogether.chatservice.dto.response.ChatRoomResponse;
import com.bytogether.chatservice.dto.response.ParticipatingStaticsResponse;
import com.bytogether.chatservice.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 채팅방 엔티티에 대한 데이터베이스 접근을 담당하는 레포지토리
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-13
 */

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT c.creatorUserId FROM ChatRoom c WHERE c.id = :id")
    Long getCreatorUserIdById(Long id);

    Optional<ChatRoom> findByMarketId(Long marketId);

    // DTO로 직접 매핑하는 깔끔한 방법
    @Query("""
        SELECT new com.bytogether.chatservice.dto.response.ParticipatingStaticsResponse(
            COUNT(CASE WHEN cr.status IN ('RECRUITING', 'RECRUITMENT_CLOSED', 'COMPLETED') AND cr.creatorUserId = :userId THEN 1 END),
            COUNT(DISTINCT CASE WHEN cr.status IN ('RECRUITING', 'RECRUITMENT_CLOSED', 'COMPLETED') AND p.isBuyer = true THEN cr.id END),
            COUNT(CASE WHEN cr.status = 'COMPLETED' THEN 1 END)
        )
        FROM ChatRoom cr
        LEFT JOIN ChatRoomParticipant p ON cr.id = p.chatRoom.id AND p.userId = :userId
        WHERE p.status = 'ACTIVE' OR cr.creatorUserId = :userId
        """)
    ParticipatingStaticsResponse getParticipatingStats(@Param("userId") Long userId);

    @Query("""
    SELECT DISTINCT cr.marketId, cr.status
    FROM ChatRoom cr
    JOIN ChatRoomParticipant p ON cr.id = p.chatRoom.id
    WHERE p.userId = :userId
    AND cr.creatorUserId != :userId
    AND p.status = 'ACTIVE'
    AND p.isBuyer = true
    """)
    List<Object[]> findUserMarketIds(@Param("userId") Long userId);

    List<ChatRoom> findByMarketIdIn(List<Long> marketIds);
}