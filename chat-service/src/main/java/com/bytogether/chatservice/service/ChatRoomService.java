package com.bytogether.chatservice.service;

import com.bytogether.chatservice.client.MarketServiceClient;
import com.bytogether.chatservice.client.UserServiceClient;
import com.bytogether.chatservice.client.dto.UserInfoRequest;
import com.bytogether.chatservice.client.dto.UserInternalResponse;
import com.bytogether.chatservice.client.dto.UsersInfoRequest;
import com.bytogether.chatservice.dto.request.ChatRoomCreateRequest;
import com.bytogether.chatservice.dto.response.*;
import com.bytogether.chatservice.entity.*;
import com.bytogether.chatservice.mapper.ChatRoomMapper;
import com.bytogether.chatservice.repository.ChatRoomParticipantHistoryRepository;
import com.bytogether.chatservice.repository.ChatRoomParticipantRepository;
import com.bytogether.chatservice.repository.ChatRoomRepository;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    private final ChatMessageService chatMessageService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final ChatRoomParticipantHistoryRepository historyRepository;
    private final ChatRoomMapper chatRoomMapper;
    private final UserServiceClient userServiceClient;
    private final MarketServiceClient marketServiceClient;


    @Transactional
    public void joinChatRoom(Long marketId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByMarketId(marketId).orElseThrow();

        if (chatRoom.getStatus() != ChatRoomStatus.RECRUITING) {
            throw new RuntimeException("모집이 마감된 채팅방입니다");
        }

        UserInternalResponse userInfo = userServiceClient.getUserInfo(UserInfoRequest.builder().userId(userId).build());

        // 1. 기존 참가 기록 확인
        Optional<ChatRoomParticipant> existingParticipant =
                participantRepository.findByUserIdAndChatRoomId(userId, chatRoom.getId());

        ChatRoomParticipant participant;
        boolean isRejoining = false;
        LocalDateTime now = LocalDateTime.now();

        if (existingParticipant.isPresent()) {
            // 재입장 처리
            participant = existingParticipant.get();

            // 강퇴자는 재입장 불가
            if (participant.getIsPermanentlyBanned()) {
                throw new ForbiddenException("강퇴된 사용자는 재입장할 수 없습니다");
            }

            // 이미 활동 중이면 중복 입장 방지
            if (participant.getStatus() == ParticipantStatus.ACTIVE) {
                throw new RuntimeException("이미 참가 중인 채팅방입니다");
            }

            // 현재 세션 리셋
            participant.setStatus(ParticipantStatus.ACTIVE);
            participant.setJoinedAt(now);
            participant.setLeftAt(null);
            participant.setListOrderTime(now);
            participant.setIsBuyer(false);
            participant.setBuyerConfirmedAt(null);

            isRejoining = true;

        } else {
            // 최초 입장
            participant = ChatRoomParticipant.builder()
                    .chatRoom(chatRoom)
                    .userId(userId)
                    .status(ParticipantStatus.ACTIVE)
                    .joinedAt(now)
                    .listOrderTime(now)
                    .isBuyer(false)
                    .isPermanentlyBanned(false)
                    .build();

            participant = participantRepository.save(participant);
        }

        ChatRoomParticipantHistory newHistory = ChatRoomParticipantHistory.builder()
                .chatRoom(chatRoom)
                .userId(userId)
                .joinedAt(participant.getJoinedAt())
                .build();

        historyRepository.save(newHistory);

        String message = userInfo.getNickName() + "님이 참가하셨습니다";

        chatMessageService.sendSystemMessage(chatRoom.getId(), message);
    }

    /**
     * 내 채팅방 목록 조회 - 최초 로드 (페이지 크기 지정)
     */
    public ChatRoomPageResponse getMyChatRooms(Long userId, int size) {
        // 활성 + 퇴장한 채팅방 모두 조회
        List<ParticipantStatus> statuses = List.of(
                ParticipantStatus.ACTIVE,
                ParticipantStatus.LEFT_RECRUITMENT_CLOSED,
                ParticipantStatus.LEFT_RECRUITMENT_CANCELED,
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
                ParticipantStatus.LEFT_RECRUITMENT_CLOSED,
                ParticipantStatus.LEFT_RECRUITMENT_CANCELED,
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

    public boolean isParticipatingByMarketId(Long marketId, Long userId) {
        // 채팅방에 참가한 상태인 유저가 맞는지 공동구매 게시글 id를 통해 확인
        ChatRoom chatRoom = chatRoomRepository.findByMarketId(marketId).orElseThrow();

        return isParticipating(chatRoom.getId(), userId);
    }

    public boolean isBuyer(Long roomId, Long userId) {
        // 공동구매에 참가한 상태인 유저가 맞는지 확인

        return participantRepository.existsByChatRoomIdAndUserIdAndIsBuyerTrue(roomId, userId);
    }

    public boolean isCreator(Long roomId, Long userId) {
        // 방장인지 확인

        return Objects.equals(userId, chatRoomRepository.getCreatorUserIdById(roomId));
    }

    public boolean isPermanentlyBanned(Long roomId, Long userId) {
        // 영구밴당한 유저인지 확인

        return participantRepository.existsByChatRoomIdAndUserIdAndIsPermanentlyBannedTrue(roomId, userId);
    }

    public boolean isPermanentlyBannedByMarketId(Long marketId, Long userId) {
        // 영구밴당한 유저인지 공동구매 게시글 id로 확인
        ChatRoom chatRoom = chatRoomRepository.findByMarketId(marketId).orElseThrow();

        return isPermanentlyBanned(chatRoom.getId(), userId);
    }

    public ChatRoomResponse getChatRoomDetails(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다. ID: " + roomId));

        Integer currentBuyers = participantRepository.countBuyersByRoomId(roomId);
        Integer currentParticipants = participantRepository.countParticipantsByRoomId(roomId);

        ChatRoomResponse chatRoomResponse = chatRoomMapper.convertToResponse(room, currentBuyers, currentParticipants);

        chatRoomResponse.setBuyer(isBuyer(roomId, userId));
        chatRoomResponse.setCreator(isCreator(roomId, userId));

        return chatRoomResponse;
    }

    public ChatRoomResponse getChatRoomDetailsByMarketId(Long marketId) {
        ChatRoom room = chatRoomRepository.findByMarketId(marketId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다. ID: " + marketId));

        Integer currentBuyers = participantRepository.countBuyersByRoomId(room.getId());
        Integer currentParticipants = participantRepository.countParticipantsByRoomId(room.getId());

        return chatRoomMapper.convertToResponse(room, currentBuyers, currentParticipants);
    }

    public ParticipantListResponse getParticipants(Long marketId) {
        // 1. 채팅방 정보 조회 (방장 ID 확인용)
        ChatRoom chatRoom = chatRoomRepository.findByMarketId(marketId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다. ID: " + marketId));

        Long creatorUserId = chatRoom.getCreatorUserId();
        Long roomId = chatRoom.getId();

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
        UsersInfoRequest usersInfoRequest = UsersInfoRequest.buildRequest(userIds);

        // 5. User 서비스에서 사용자 정보 조회 (MSA)
        List<UserInternalResponse> userInfoList = userServiceClient.getUsersInfo(usersInfoRequest);

        Map<Long, UserInternalResponse> userInfoMap = userInfoList.stream()
                .collect(Collectors.toMap(
                        UserInternalResponse::getUserId,  // 키: userId
                        userInfo -> userInfo              // 값: UserInternalResponse 객체
                ));

        // 6. ParticipantResponse 리스트 생성
        List<ParticipantResponse> participantResponses = participants.stream()
                .map(participant -> {
                    Long userId = participant.getUserId();
                    UserInternalResponse userInfo = userInfoMap.get(userId);

                    return ParticipantResponse.builder()
                            .userId(userId)
                            .nickname(userInfo != null ? userInfo.getNickName() : "알 수 없음")
                            .profileImage(userInfo != null ? userInfo.getImageUrl() : null)
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
                .roomId(roomId)
                .currentParticipants(currentParticipants)
                .currentBuyers(currentBuyers)
                .participants(participantResponses)
                .build();
    }

    public ParticipatingStaticsResponse countMyChatrooms(Long userId) {
        return chatRoomRepository.getParticipatingStats(userId);
    }

    public UserMarketIdsResponse getUserMarketIds(Long userId) {
        List<Object[]> results = chatRoomRepository.findUserMarketIds(userId);

        List<Long> ongoing = new ArrayList<>();
        List<Long> completed = new ArrayList<>();

        for (Object[] row : results) {
            Long marketId = (Long) row[0];
            ChatRoomStatus status = (ChatRoomStatus) row[1];

            switch (status) {
                case RECRUITING, RECRUITMENT_CLOSED -> ongoing.add(marketId);
                case COMPLETED -> completed.add(marketId);
            }
        }

        return UserMarketIdsResponse.builder()
                .ongoing(ongoing)
                .completed(completed)
                .ongoingCount(ongoing.size())
                .completedCount(completed.size())
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

        ChatRoomParticipant saved = participantRepository.save(participant);

        // ChatRoomParticipantHistory 테이블에 기록
        historyRepository.updateLeftAtAndExitType(saved.getChatRoom().getId(), userId, saved.getLeftAt(), ExitType.VOLUNTARY);

        UserInternalResponse userInfo = userServiceClient.getUserInfo(UserInfoRequest.builder().userId(userId).build());

        String system = userInfo.getNickName() + "님이 퇴장하셨습니다";

        chatMessageService.sendSystemMessage(roomId, system);

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
        historyRepository.updateLeftAtAndExitType(targetUserId, roomId, participant.getLeftAt(), ExitType.KICKED);

        UserInternalResponse targetUserInfo = userServiceClient.getUserInfo(UserInfoRequest.builder().userId(targetUserId).build());

        String system = targetUserInfo.getNickName() + "님이 강퇴되셨습니다";

        chatMessageService.sendSystemMessage(roomId, system);

        chatMessageService.notifyKickedUser(targetUserId, roomId, null);

        return "kickedAt: " + LocalDateTime.now();
    }

    @Transactional
    public BuyerConfirmResponse confirmBuyer(Long roomId, Long userId) {
        ChatRoomParticipant participant = participantRepository
                .findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new NotFoundException("참가 정보를 찾을 수 없습니다"));

        participant.confirmBuyer();

        participantRepository.save(participant);

        int currentBuyers = participantRepository.countBuyersByRoomId(roomId);

        UserInternalResponse userInfo = userServiceClient.getUserInfo(UserInfoRequest.builder().userId(userId).build());

        String system = userInfo.getNickName() + "님이 공동구매에 참가하셨습니다\n현재 구매자 : " + currentBuyers;

        chatMessageService.sendSystemMessage(roomId, system);

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

        UserInternalResponse userInfo = userServiceClient.getUserInfo(UserInfoRequest.builder().userId(userId).build());

        String system = userInfo.getNickName() + "님이 공동구매 참가를 취소하셨습니다\n현재 구매자 : " + currentBuyers;

        chatMessageService.sendSystemMessage(roomId, system);

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

        String system = "공동구매 모집 마감기한이 " + hours + "시간 연장되었습니다";

        chatMessageService.sendExtendMessage(roomId, system);

        return ExtendDeadlineResponse.builder()
                .roomId(roomId)
                .newDeadline(newDeadline)
                .extendedHours(hours)
                .build();
    }

    @Transactional
    public RecruitmentCloseResponse closeRecruitment(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다"));

        // 채팅방 상태 변경
        room.setStatus(ChatRoomStatus.RECRUITMENT_CLOSED);
        room.setRecruitmentClosedAt(LocalDateTime.now());

        ChatRoom saved = chatRoomRepository.save(room);

        // 구매자가 아닌 참가자들 강제 퇴장
        List<ChatRoomParticipant> nonBuyers = participantRepository
                .findByChatRoomIdAndStatusAndIsBuyerFalse(roomId, ParticipantStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();

        for(ChatRoomParticipant participant : nonBuyers) {
            participant.setStatus(ParticipantStatus.LEFT_RECRUITMENT_CLOSED);
            participant.setLeftAt(now);

            chatMessageService.notifyUser(participant.getUserId(), roomId, "공동구매 모집이 마감되어 자동으로 퇴장되었습니다");
        }

        participantRepository.saveAll(nonBuyers);

        List<Long> userIds = nonBuyers.stream()
                .map(ChatRoomParticipant::getUserId)
                .toList();

        int updatedCount = historyRepository.batchUpdateLeftAtAndExitType(
                userIds,
                roomId,
                now,
                ExitType.NOT_BUYER
        );

        int finalBuyers = participantRepository.countBuyersByRoomId(roomId);

        String system = "공동구매 모집이 마감되었습니다!"
                +"\n최종 인원 : " + finalBuyers;

        chatMessageService.sendSystemMessage(roomId, system);


        // market-service에 상태변경 요청
        marketServiceClient.completeMarketPost(room.getMarketId(), userId);

        return RecruitmentCloseResponse.builder()
                .roomId(roomId)
                .closedAt(saved.getRecruitmentClosedAt())
                .finalBuyerCount(finalBuyers)
                .kickedCount(nonBuyers.size())
                .build();
    }

    @Transactional
    public RecruitmentCancelResponse cancelRecruitment(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다"));

        // 채팅방 상태 변경
        room.setStatus(ChatRoomStatus.CANCELLED);

        ChatRoom saved = chatRoomRepository.save(room);

        // 참가자들 강제 퇴장
        List<ChatRoomParticipant> participants = participantRepository
                .findByChatRoomIdAndStatus(roomId, ParticipantStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();

        for(ChatRoomParticipant participant : participants) {
            participant.setStatus(ParticipantStatus.LEFT_RECRUITMENT_CANCELED);
            participant.setLeftAt(now);

            chatMessageService.notifyUser(participant.getUserId(), roomId, "공동구매 모집이 취소되어 자동으로 퇴장되었습니다");
        }

        participantRepository.saveAll(participants);

        List<Long> userIds = participants.stream()
                .map(ChatRoomParticipant::getUserId)
                .toList();

        int updatedCount = historyRepository.batchUpdateLeftAtAndExitType(
                userIds,
                roomId,
                now,
                ExitType.NOT_BUYER
        );

        String system = "공동구매 모집이 취소되었습니다";

        chatMessageService.sendSystemMessage(roomId, system);


        // market-service에 상태변경 요청
        marketServiceClient.cancelMarketPost(room.getMarketId(), userId);


        return RecruitmentCancelResponse.builder()
                .roomId(roomId)
                .canceledAt(saved.getUpdatedAt())
                .build();
    }

    @Transactional
    public String completePurchase(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다"));

        String system = "공동구매가 완료되었습니다";

        chatMessageService.sendSystemMessage(roomId, system);

        List<ChatRoomParticipant> participants = participantRepository
                .findByChatRoomIdAndStatus(roomId, ParticipantStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();

        for(ChatRoomParticipant participant : participants) {
            participant.setStatus(ParticipantStatus.LEFT_COMPLETED);
            participant.setLeftAt(now);

            chatMessageService.notifyUser(participant.getUserId(), roomId, "공동구매가 완료되었습니다\n즐거운 공동구매 되셨길 바랍니다");
        }

        participantRepository.saveAll(participants);

        List<Long> userIds = participants.stream()
                .map(ChatRoomParticipant::getUserId)
                .toList();

        int updatedCount = historyRepository.batchUpdateLeftAtAndExitType(
                userIds,
                roomId,
                now,
                ExitType.COMPLETED
        );

//        log.info("참가자 이력 업데이트 완료 - roomId: {}, count: {}", roomId, updatedCount);

        room.setStatus(ChatRoomStatus.COMPLETED);
        room.setCompletedAt(LocalDateTime.now());

        chatRoomRepository.save(room);

        return "completedAt: " + room.getCompletedAt();
    }


    // 채팅방 생성
    @Transactional
    public ChatRoomResponse createRoom(ChatRoomCreateRequest request) {
        ChatRoom room = request.toChatRoom();

        ChatRoom saved = chatRoomRepository.saveAndFlush(room);

        joinChatRoom(saved.getMarketId(), request.getCreatorUserId());

        confirmBuyer(saved.getId(), request.getCreatorUserId());

        return chatRoomMapper.convertToResponse(saved, 1, 1);
    }
}
