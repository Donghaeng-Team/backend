package com.bytogether.chatservice.repository;

import com.bytogether.chatservice.dto.response.ChatRoomResponse;
import com.bytogether.chatservice.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 채팅방 참가자 엔티티에 대한 데이터베이스 접근을 담당하는 레포지토리
 *
 * v1.01
 * 스크롤 로딩용 메서드 구현
 *
 * v1.02
 * 현재 채팅방에 참가중인 인원 목록을 쿼리하는 메서드 구현
 *
 * @author jhj010311@gmail.com
 * @version 1.02
 * @since 2025-10-16
 */

@Repository
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    List<ChatRoomParticipant> findByChatRoomIdAndIsBuyerFalse(Long chatRoomId);

    List<ChatRoomParticipant> findByUserIdAndStatus(Long userId, ParticipantStatus status);

    boolean existsByChatRoomIdAndUserIdAndIsPermanentlyBannedTrue(Long chatRoomId, Long userId);

    boolean existsByChatRoomIdAndUserIdAndStatus(Long chatRoomId, Long userId, ParticipantStatus participantStatus);

    boolean existsByChatRoomIdAndUserIdAndIsBuyerTrue(Long roomId, Long userId);


    /**
     * 내 채팅방 목록 - 최초 로드
     * listOrderTime을 커서로 사용
     */
    @Query("""
            SELECT p FROM ChatRoomParticipant p
            JOIN FETCH p.chatRoom cr
            WHERE p.userId = :userId
            AND p.status IN :statuses
            ORDER BY p.listOrderTime DESC, p.id DESC
            """)
    List<ChatRoomParticipant> findMyRecentChatRooms(
            @Param("userId") Long userId,
            @Param("statuses") List<ParticipantStatus> statuses,
            Pageable pageable);

    /**
     * 내 채팅방 목록 - 다음 페이지 (커서 기반)
     * listOrderTime이 cursor보다 이전인 것들 조회
     */
    @Query("""
            SELECT p FROM ChatRoomParticipant p
            JOIN FETCH p.chatRoom cr
            WHERE p.userId = :userId
            AND p.status IN :statuses
            AND (p.listOrderTime < :cursor
                OR (p.listOrderTime = :cursor AND p.id < :id))
            ORDER BY p.listOrderTime DESC, p.id DESC
            """)
    List<ChatRoomParticipant> findMyChatRoomsBeforeCursor(
            @Param("userId") Long userId,
            @Param("statuses") List<ParticipantStatus> statuses,
            @Param("cursor") LocalDateTime cursor,
            @Param("id") Long id,
            Pageable pageable);


    // 채팅방별 구매자 수 조회
    @Query("""
        SELECT p.chatRoom.id, COUNT(p)
        FROM ChatRoomParticipant p
        WHERE p.chatRoom.id IN :roomIds
        AND p.status = 'ACTIVE'
        AND p.isBuyer = true
        GROUP BY p.chatRoom.id
        """)
    List<Object[]> countBuyersByRoomIdsRaw(@Param("roomIds") List<Long> roomIds);

    default Map<Long, Integer> countBuyersByRoomIds(List<Long> roomIds) {
        if (roomIds.isEmpty()) return Collections.emptyMap();

        return countBuyersByRoomIdsRaw(roomIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
    }

    // 단일 채팅방 구매자 수 조회
    @Query("""
        SELECT COUNT(p)
        FROM ChatRoomParticipant p
        WHERE p.chatRoom.id = :roomId
        AND p.status = 'ACTIVE'
        AND p.isBuyer = true
        """)
    int countBuyersByRoomId(@Param("roomId") Long roomId);

    // 단일 채팅방 참가자 수 조회
    @Query("""
        SELECT COUNT(p)
        FROM ChatRoomParticipant p
        WHERE p.chatRoom.id = :roomId
        AND p.status = 'ACTIVE'
        """)
    int countParticipantsByRoomId(@Param("roomId") Long roomId);

    @Query("""
        SELECT p FROM ChatRoomParticipant p
        WHERE p.chatRoom.id = :roomId
        AND p.status = :status
        ORDER BY p.firstJoinedAt ASC
        """)
    List<ChatRoomParticipant> findByChatRoomIdAndStatus(@Param("roomId") Long roomId, @Param("status") ParticipantStatus status);
}