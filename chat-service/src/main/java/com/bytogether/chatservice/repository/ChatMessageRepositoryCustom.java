package com.bytogether.chatservice.repository;

import com.bytogether.chatservice.dto.common.ViewablePeriod;
import com.bytogether.chatservice.entity.ChatMessage;
import com.bytogether.chatservice.entity.MessageType;
import com.bytogether.chatservice.service.ChatMessageService;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepositoryCustom {

    // 복수의 시간 구간에서 메시지 조회
    List<ChatMessage> findMessagesInPeriods(
            Long chatRoomId,
            List<ViewablePeriod> periods,
            Pageable pageable
    );

    // 커서 기반 + 복수 구간
    List<ChatMessage> findMessagesInPeriodsBeforeCursor(
            Long chatRoomId,
            Long cursorId,
            List<ViewablePeriod> periods,
            Pageable pageable
    );
//
//    // 동적 조건으로 메시지 검색
//    List<ChatMessage> searchMessages(
//            Long chatRoomId,
//            String keyword,
//            MessageType messageType,
//            LocalDateTime startDate,
//            LocalDateTime endDate,
//            Pageable pageable
//    );
}