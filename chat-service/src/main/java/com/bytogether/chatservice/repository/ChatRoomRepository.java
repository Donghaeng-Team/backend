package com.bytogether.chatservice.repository;

import com.bytogether.chatservice.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 채팅방 엔티티에 대한 데이터베이스 접근을 담당하는 레포지토리
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-13
 */

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}