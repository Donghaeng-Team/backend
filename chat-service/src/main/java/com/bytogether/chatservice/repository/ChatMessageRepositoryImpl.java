package com.bytogether.chatservice.repository;

import com.bytogether.chatservice.dto.common.ViewablePeriod;
import com.bytogether.chatservice.entity.ChatMessage;
import com.bytogether.chatservice.entity.MessageType;
import com.bytogether.chatservice.service.ChatMessageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<ChatMessage> findMessagesInPeriods(
            Long chatRoomId,
            List<ViewablePeriod> periods,
            Pageable pageable) {

        // 1. 기본 쿼리
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT m FROM ChatMessage m ");
        jpql.append("WHERE m.chatRoom.id = :chatRoomId ");
        jpql.append("AND m.isDeleted = false ");

        // 2. 동적 조건 추가 (시간 구간)
        Map<String, Object> params = new HashMap<>();
        params.put("chatRoomId", chatRoomId);

        if (!periods.isEmpty()) {
            jpql.append("AND (");
            for (int i = 0; i < periods.size(); i++) {
                if (i > 0) jpql.append(" OR ");

                String fromParam = "from" + i;
                String untilParam = "until" + i;

                jpql.append("(m.sentAt >= :").append(fromParam);
                params.put(fromParam, periods.get(i).getFrom());

                if (periods.get(i).getUntil() != null) {
                    jpql.append(" AND m.sentAt <= :").append(untilParam);
                    params.put(untilParam, periods.get(i).getUntil());
                }
                jpql.append(")");
            }
            jpql.append(") ");
        }

        // 3. 정렬
        jpql.append("ORDER BY m.sentAt DESC");

        // 4. 쿼리 생성 및 파라미터 바인딩
        TypedQuery<ChatMessage> query = em.createQuery(jpql.toString(), ChatMessage.class);
        params.forEach(query::setParameter);

        // 5. 페이징
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }

    /**
     * 커서 기반 + 복수 시간 구간 메시지 조회
     */
    @Override
    public List<ChatMessage> findMessagesInPeriodsBeforeCursor(
            Long chatRoomId,
            Long cursorId,
            List<ViewablePeriod> periods,
            Pageable pageable) {

        // 1. 커서 메시지 조회
        ChatMessage cursor = em.find(ChatMessage.class, cursorId);
        if (cursor == null) {
            return new ArrayList<>();
        }

        // 2. 기본 쿼리 구성
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT m FROM ChatMessage m ");
        jpql.append("WHERE m.chatRoom.id = :chatRoomId ");
        jpql.append("AND m.isDeleted = false ");

        // 3. 커서 조건 (sentAt이 같을 수 있으므로 id도 함께 비교)
        jpql.append("AND (");
        jpql.append("  m.sentAt < :cursorSentAt ");
        jpql.append("  OR (m.sentAt = :cursorSentAt AND m.id < :cursorId)");
        jpql.append(") ");

        // 4. 파라미터 맵
        Map<String, Object> params = new HashMap<>();
        params.put("chatRoomId", chatRoomId);
        params.put("cursorSentAt", cursor.getSentAt());
        params.put("cursorId", cursorId);

        // 5. 시간 구간 조건 추가
        if (!periods.isEmpty()) {
            jpql.append("AND (");
            for (int i = 0; i < periods.size(); i++) {
                if (i > 0) jpql.append(" OR ");

                String fromParam = "from" + i;
                String untilParam = "until" + i;

                jpql.append("(m.sentAt >= :").append(fromParam);
                params.put(fromParam, periods.get(i).getFrom());

                if (periods.get(i).getUntil() != null) {
                    jpql.append(" AND m.sentAt <= :").append(untilParam);
                    params.put(untilParam, periods.get(i).getUntil());
                }
                jpql.append(")");
            }
            jpql.append(") ");
        }

        // 6. 정렬 (중요: 커서 기반에서는 일관된 정렬 필수)
        jpql.append("ORDER BY m.sentAt DESC, m.id DESC");

        // 7. 쿼리 실행
        TypedQuery<ChatMessage> query = em.createQuery(jpql.toString(), ChatMessage.class);
        params.forEach(query::setParameter);

        // 8. 페이징
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }
}