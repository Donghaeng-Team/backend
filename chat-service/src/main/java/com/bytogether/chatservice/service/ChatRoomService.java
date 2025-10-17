package com.bytogether.chatservice.service;

import com.bytogether.chatservice.dto.response.*;
import com.bytogether.chatservice.entity.*;
import com.bytogether.chatservice.mapper.ChatRoomMapper;
import com.bytogether.chatservice.repository.ChatRoomParticipantHistoryRepository;
import com.bytogether.chatservice.repository.ChatRoomParticipantRepository;
import com.bytogether.chatservice.repository.ChatRoomRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 채팅방 목록 담당 서비스
 *
 * @author jhj010311@gmail.com
 * @version 1.01
 * @since 2025-10-17
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {
    ChatRoomRepository chatRoomRepository;
    ChatRoomParticipantRepository participantRepository;
    ChatRoomParticipantHistoryRepository historyRepository;
    ChatRoomMapper chatRoomMapper;

    /**
     * 내 채팅방 목록 조회 - 최초 로드 (페이지 크기 지정)
     */
    public ChatRoomPageResponse getMyChatRooms(Long userId, int size) {
        // 활성 + 퇴장한 채팅방 모두 조회
        List<ParticipantStatus> statuses = List.of(
                ParticipantStatus.ACTIVE,
                ParticipantStatus.LEFT_NOT_BUYER,
                ParticipantStatus.LEFT_COMPLETED
        );

        // N+1개 조회하여 다음 페이지 존재 여부 확인
        Pageable pageable = PageRequest.of(0, size + 1);

        List<ChatRoomParticipant> userParticipations  = participantRepository
                .findMyRecentChatRooms(userId, statuses, pageable);

        // 참여한 채팅방의 id들 추출
        List<Long> roomIds = userParticipations.stream()
                .map(p -> p.getChatRoom().getId())
                .collect(Collectors.toList());

        // 추출한 채팅방 id로 공동구매 참가자 수 획득
        Map<Long, Integer> buyerCounts = participantRepository.countBuyersByRoomIds(roomIds);

        return chatRoomMapper.buildPageResponse(userParticipations, buyerCounts, size);
    }

    /**
     * 내 채팅방 목록 조회 - 커서 기반 (다음 페이지)
     */
    public ChatRoomPageResponse getMyChatRooms(
            Long userId,
            LocalDateTime cursor,
            Long participantId,
            int size) {

        List<ParticipantStatus> statuses = List.of(
                ParticipantStatus.ACTIVE,
                ParticipantStatus.LEFT_NOT_BUYER,
                ParticipantStatus.LEFT_COMPLETED
        );

        Pageable pageable = PageRequest.of(0, size + 1);

        List<ChatRoomParticipant> userParticipations = participantRepository
                .findMyChatRoomsBeforeCursor(userId, statuses, cursor, participantId, pageable);

        // 참여한 채팅방의 id들 추출
        List<Long> roomIds = userParticipations.stream()
                .map(p -> p.getChatRoom().getId())
                .collect(Collectors.toList());

        // 추출한 채팅방 id로 공동구매 참가자 수 획득
        Map<Long, Integer> buyerCounts = participantRepository.countBuyersByRoomIds(roomIds);

        return chatRoomMapper.buildPageResponse(userParticipations, buyerCounts, size);
    }

    public boolean isParticipating(Long roomId, Long userId) {
        // 채팅방에 참가한 상태인 유저가 맞는지 확인

        return participantRepository.existsByChatRoomIdAndUserIdAndStatus(roomId, userId, ParticipantStatus.ACTIVE);
    }

    public boolean isBuyer(Long roomId, Long userId) {
        // 공동구매에 참가한 상태인 유저가 맞는지 확인

        return participantRepository.existsByChatRoomIdAndUserIdAndIsBuyerTrue(roomId, userId);
    }

    public boolean isCreator(Long roomId, Long userId) {
        // 방장인지 확인

        return userId == chatRoomRepository.getCreatorUserIdById(roomId);
    }

    public ChatRoomResponse getChatRoomDetails(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다. ID: " + roomId));

        Integer currentBuyers = participantRepository.countBuyersByRoomId(roomId);
        Integer currentParticipants = participantRepository.countParticipantsByRoomId(roomId);

        return chatRoomMapper.convertToResponse(room, currentBuyers, currentParticipants);
    }

    public ParticipantListResponse getParticipants(Long roomId) {
        // 1. 채팅방 정보 조회 (방장 ID 확인용)
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다. ID: " + roomId));

        Long creatorUserId = chatRoom.getCreatorUserId();

        // 2. 활성 참가자만 조회
        List<ChatRoomParticipant> participants = participantRepository
                .findByChatRoomIdAndStatus(roomId, ParticipantStatus.ACTIVE);

        // 3. 참가자 수 계산
        int currentParticipants = participants.size();
        int currentBuyers = (int) participants.stream()
                .filter(ChatRoomParticipant::getIsBuyer)
                .count();

        // 4. User ID 목록 추출
        List<Long> userIds = participants.stream()
                .map(ChatRoomParticipant::getUserId)
                .toList();

//        // 5. User 서비스에서 사용자 정보 조회 (MSA)
//        Map<Long, UserInfo> userInfoMap = userServiceClient.getUsersByIds(userIds);

        // 6. ParticipantResponse 리스트 생성
        List<ParticipantResponse> participantResponses = participants.stream()
                .map(participant -> {
                    Long userId = participant.getUserId();
//                    UserInfo userInfo = userInfoMap.get(userId);

                    return ParticipantResponse.builder()
                            .userId(userId)
//                            .nickname(userInfo != null ? userInfo.getNickname() : "알 수 없음")
//                            .profileImage(userInfo != null ? userInfo.getProfileImage() : null)
                            .isBuyer(participant.getIsBuyer())
                            .isCreator(userId.equals(creatorUserId))
                            .joinedAt(participant.getJoinedAt())
                            .build();
                }).sorted((a, b) -> {
                    // 7. 정렬 (방장 > 구매자 > 일반 참가자 > 가입 시간 순)
                    // 방장이 최우선
                    if (a.getIsCreator() && !b.getIsCreator()) return -1;
                    if (!a.getIsCreator() && b.getIsCreator()) return 1;

                    // 구매자가 다음 우선
                    if (a.getIsBuyer() && !b.getIsBuyer()) return -1;
                    if (!a.getIsBuyer() && b.getIsBuyer()) return 1;

                    // 나머지는 가입 시간 순
                    return a.getJoinedAt().compareTo(b.getJoinedAt());
                }).collect(Collectors.toList());

        return ParticipantListResponse.builder()
                .currentParticipants(currentParticipants)
                .currentBuyers(currentBuyers)
                .participants(participantResponses)
                .build();
    }

    @Transactional
    public String leaveChatRoom(Long roomId, Long userId) {
        ChatRoomParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new NotFoundException("참가 정보를 찾을 수 없습니다"));

        // ChatRoomParticipant 테이블 업데이트
        participant.setIsBuyer(false);
        participant.setBuyerConfirmedAt(null);
        participant.setLeftAt(LocalDateTime.now());
        participant.setStatus(ParticipantStatus.LEFT_VOLUNTARY);
        participant.setUpdatedAt(LocalDateTime.now());

        participantRepository.save(participant);

        // ChatRoomParticipantHistory 테이블에 기록
        ChatRoomParticipantHistory history = ChatRoomParticipantHistory.builder()
                .chatRoom(participant.getChatRoom())
                .userId(userId)
                .joinedAt(participant.getJoinedAt())
                .leftAt(LocalDateTime.now())
                .exitType(ExitType.VOLUNTARY)
                .build();

        historyRepository.save(history);

        return "leftAt: " + participant.getLeftAt();
    }

    @Transactional
    public String kickParticipant(Long roomId, Long targetUserId) {
        ChatRoomParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(roomId, targetUserId)
                .orElseThrow(() -> new NotFoundException("참가자를 찾을 수 없습니다"));

        participant.setStatus(ParticipantStatus.BANNED);
        participant.setLeftAt(LocalDateTime.now());
        participant.setIsBuyer(false);
        participant.setBuyerConfirmedAt(null);

        participantRepository.save(participant);

        // 히스토리 기록
        ChatRoomParticipantHistory history = ChatRoomParticipantHistory.builder()
                .chatRoom(participant.getChatRoom())
                .userId(targetUserId)
                .joinedAt(participant.getJoinedAt())
                .leftAt(LocalDateTime.now())
                .exitType(ExitType.KICKED)
                .build();

        historyRepository.save(history);

        return "kickedAt: " + LocalDateTime.now();
    }

    @Transactional
    public BuyerConfirmResponse confirmBuyer(Long roomId, Long userId) {
        ChatRoomParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new NotFoundException("참가 정보를 찾을 수 없습니다"));

        participant.setIsBuyer(true);
        participant.setBuyerConfirmedAt(LocalDateTime.now());

        participantRepository.save(participant);

        int currentBuyers = participantRepository.countBuyersByRoomId(roomId);

        return BuyerConfirmResponse.builder()
                .userId(userId)
                .isBuyer(true)
                .confirmedAt(LocalDateTime.now())
                .currentBuyers(currentBuyers)
                .build();
    }

    @Transactional
    public BuyerConfirmResponse cancelBuyer(Long roomId, Long userId) {
        ChatRoomParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new NotFoundException("참가 정보를 찾을 수 없습니다"));

        participant.setIsBuyer(false);
        participant.setBuyerConfirmedAt(null);

        participantRepository.save(participant);

        int currentBuyers = participantRepository.countBuyersByRoomId(roomId);

        return BuyerConfirmResponse.builder()
                .userId(userId)
                .isBuyer(false)
                .confirmedAt(null)
                .currentBuyers(currentBuyers)
                .build();
    }

    @Transactional
    public ExtendDeadlineResponse extendDeadline(Long roomId, Integer hours) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다"));

        LocalDateTime newDeadline = room.getEndTime().plusHours(hours);
        room.setEndTime(newDeadline);
        room.setUpdatedAt(LocalDateTime.now());

        chatRoomRepository.save(room);

        return ExtendDeadlineResponse.builder()
                .roomId(roomId)
                .newDeadline(newDeadline)
                .extendedHours(hours)
                .build();
    }

    @Transactional
    public RecruitmentCloseResponse closeRecruitment(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다"));

        room.setStatus(ChatRoomStatus.RECRUITMENT_CLOSED);
        room.setRecruitmentClosedAt(LocalDateTime.now());

        // 구매자가 아닌 참가자들 강제 퇴장
        List<ChatRoomParticipant> nonBuyers = participantRepository
                .findByChatRoomIdAndStatusAndIsBuyerFalse(roomId, ParticipantStatus.ACTIVE);

        for(ChatRoomParticipant participant : nonBuyers) {
            participant.setStatus(ParticipantStatus.LEFT_NOT_BUYER);
            participant.setLeftAt(LocalDateTime.now());
        }

        participantRepository.saveAll(nonBuyers);

        int finalBuyers = participantRepository.countBuyersByRoomId(roomId);

        return RecruitmentCloseResponse.builder()
                .roomId(roomId)
                .closedAt(LocalDateTime.now())
                .finalBuyerCount(finalBuyers)
                .kickedCount(nonBuyers.size())
                .build();
    }

    @Transactional
    public String completePurchase(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다"));

        room.setStatus(ChatRoomStatus.COMPLETED);
        room.setCompletedAt(LocalDateTime.now());

        chatRoomRepository.save(room);

        return "completedAt: " + room.getCompletedAt();
    }
}
