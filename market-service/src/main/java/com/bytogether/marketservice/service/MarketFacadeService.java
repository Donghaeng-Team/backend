package com.bytogether.marketservice.service;

import com.bytogether.marketservice.client.dto.request.ChatRoomCreateRequest;
import com.bytogether.marketservice.client.dto.response.DivisionResponseDto;
import com.bytogether.marketservice.client.dto.response.ParticipantListResponse;
import com.bytogether.marketservice.client.dto.response.ParticipantListResponseWrap;
import com.bytogether.marketservice.client.dto.response.UserInternalResponse;
import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.dto.request.*;
import com.bytogether.marketservice.dto.response.*;
import com.bytogether.marketservice.entity.Market;
import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.service.sub.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 공동 구매 마켓 관련 Facade 서비스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@Service
@RequiredArgsConstructor
@Transactional
public class MarketFacadeService {
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final MarketService marketService;
    private final SearchService searchService;
    private final ViewService viewService;

    private final DivisionService divisionService;
    private final UserService userService;
    private final ChatService chatService;

    private static ChatRoomCreateRequest getChatRoomCreateRequest(Market newMarket, String thumbnailUrl) {
        ChatRoomCreateRequest chatRoomCreateRequest = new ChatRoomCreateRequest();
        chatRoomCreateRequest.setMarketId(newMarket.getId());
        chatRoomCreateRequest.setCreatorUserId(newMarket.getAuthorId());
        chatRoomCreateRequest.setEndTime(newMarket.getEndTime());
        chatRoomCreateRequest.setMinBuyers(newMarket.getRecruitMin());
        chatRoomCreateRequest.setMaxBuyers(newMarket.getRecruitMax());
        chatRoomCreateRequest.setThumbnailUrl(thumbnailUrl);
        chatRoomCreateRequest.setTitle(newMarket.getTitle());
        return chatRoomCreateRequest;
    }

    // 마켓글 작성 - private
    public CreateMarketResponse createMarketPost(Long requestUserID, CreateMarketRequest createMarketRequest) {

        // categoryId 유효성 검사
        categoryService.validateCategoryId(createMarketRequest.getCategoryId());

        // 좌표 기반 행정구역 조회
        DivisionResponseDto division = divisionService.getDivisionByCoord(createMarketRequest.getLatitude().doubleValue(), createMarketRequest.getLongitude().doubleValue());

        // 이미지 파일 MIME 타입 검사
        imageService.isAllowedMimeType(createMarketRequest.getImages());

        // 마켓글 생성
        Market newMarket = Market.fromCreateRequest(createMarketRequest, requestUserID, division.getEmdName(), division.getId());

        Market savedMarket = marketService.saveMarket(newMarket);


        // 이미지 파일 처리
        List<MultipartFile> images = createMarketRequest.getImages();
        String thumbnailUrl = imageService.handleImageWhenMarketCreate(images, savedMarket.getId());

        // 채팅방 생성
        ChatRoomCreateRequest chatRoomCreateRequest = getChatRoomCreateRequest(newMarket, thumbnailUrl);

        System.out.println("==========================");
        System.out.println("chatRoomCreateRequest = " + chatRoomCreateRequest);

        // 비동기 고려
        chatService.createChatRoom(chatRoomCreateRequest);

        return CreateMarketResponse.fromEntity(savedMarket);
    }

    // 마켓글 삭제 (취소) - private
    public void deleteMarketPost(Long requestUserID, Long marketId) {

        // 마켓글 가져오기
        Market market = marketService.findByMarketId(marketId);

        // 권한 확인
        if (!market.getAuthorId().equals(requestUserID)) {
            throw new MarketException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        // 마켓글 상태 확인 ( 모집 완료는 삭제 불가 )
        if (market.getStatus() == MarketStatus.ENDED) {
            throw new MarketException("모집 완료된 마켓글은 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }


        // 마켓글 상태 변경 (취소)
        marketService.changeStatus(market, MarketStatus.CANCELLED);
    }

    // 특정 사용자가 작성한 마켓글 조회
    public MarketListResponse getSomeonesMarketPosts(Long targetUserId, @Valid DefaultPageRequest defaultPageRequest) {

        // 특정 사용자가 작성한 마켓글 조회
        Page<Market> myMarkets = marketService.getMarketsByAuthorId(targetUserId, PageRequest.of(defaultPageRequest.getPageNum(), defaultPageRequest.getPageSize()));

        // 작성자 정보 조회 (User Service API 호출)
        UserInternalResponse userById = userService.getUserById(targetUserId);

        List<UserInternalResponse> users = new ArrayList<>();
        for (int i = 0; i < myMarkets.getContent().size(); i++) {
            users.add(userById);
        }

        List<ParticipantListResponseWrap> participantsWrap = chatService.getParticipantsWrap(myMarkets.getContent().stream().map(Market::getId).toList());

        // 응답 생성하기
        MarketListResponse marketListResponse = MarketListResponse.fromEntities(myMarkets, users, participantsWrap);


        // 응답 반환
        return marketListResponse;
    }

    // 마켓글 수정 - private
    public PutMarketResponse updateMarketPost(Long requestUserID, Long marketId, @Valid PutMarketRequest putMarketRequest) {
        // 마켓글 가져오기
        Market market = marketService.findByMarketId(marketId);

        // 권한 확인
        if (!market.getAuthorId().equals(requestUserID)) {
            throw new MarketException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 마켓글 상태 확인 ( 모집중 상태만 수정 가능 )
        if (market.getStatus() != MarketStatus.RECRUITING) {
            throw new MarketException("모집중 상태의 마켓글만 수정할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }

        // categoryId 유효성 검사
        categoryService.validateCategoryId(putMarketRequest.getCategoryId());

        // 이미지 파일 MIME 타입 검사
        imageService.isAllowedMimeType(putMarketRequest.getImages());

        // 마켓글 업데이트
        market.updateFromPutRequest(putMarketRequest);

        // 변경된 마켓 저장
        Market saveMarket = marketService.saveMarket(market);

        // 이미지 파일 처리
        List<MultipartFile> images = putMarketRequest.getImages();
        imageService.handleImageWhenUpdate(images, market.getId());

        return PutMarketResponse.fromEntity(saveMarket);
    }

    // 마켓글 연장 - private
    public ExtendMarketResponse extendMarketPost(Long requestUserID, Long marketId, ExtendMarketRequest extendMarketRequest) {
        // 마켓글 가져오기
        Market market = marketService.findByMarketId(marketId);

        // 권한 확인
        if (!market.getAuthorId().equals(requestUserID)) {
            throw new MarketException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 마켓글 상태 확인 ( 모집중 상태만 수정 가능 )
        if (market.getStatus() != MarketStatus.RECRUITING) {
            throw new MarketException("모집중 상태의 마켓글만 연장할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }

        // 마켓글 연장
        market.extendEndTimeFromExtendRequest(extendMarketRequest);
        marketService.saveMarket(market);

        return ExtendMarketResponse.fromEntity(market);
    }

    // 마켓글 상세 조회 - 비로그인은 requestUserID null로 호출
    public MarketDetailResponse getMarketPostDetailWithLogin(Long requestUserID, Long marketId) {
        // 마켓글 가져오기
        Market byMarketId = marketService.findByMarketId(marketId);

        // 조회수 증가 및 기록
        if (requestUserID != null) {
            viewService.recordViewAndIncrement(requestUserID, marketId);
        } else {
            viewService.incrementViewCount(marketId);
        }

        // 응답 생성하기
        MarketDetailResponse marketDetailResponse = MarketDetailResponse.fromEntity(byMarketId);

        // 작성자 닉네임, 프로필 이미지 URL 조회 (User Service API 호출)
        UserInternalResponse userById = userService.getUserById(byMarketId.getAuthorId());


        marketDetailResponse.setAuthorNickname(userById.getNickName());
        marketDetailResponse.setAuthorProfileImageUrl(userById.getImageUrl());


        // 현재 모집 참여 인원 수 조회 (chat Service API 호출) - TODO: 추후 구현 - 2025-10-10 // 251023 14:00 작성
        ParticipantListResponse participants = chatService.getParticipants(marketId);


        // 251023 14:00 작성 - 작동하는지 확인해야함
        marketDetailResponse.setRecruitNow(participants.getCurrentBuyers());
        marketDetailResponse.setParticipants(participants.getParticipants());
        marketDetailResponse.setChatRoomId(participants.getRoomId());


        return marketDetailResponse;
    }

    //  마켓글 목록 조회 - 비로그인은 requestUserID null로 호출
    public MarketListResponse getMarketPosts(Long requestUserID, MarketListRequest marketListRequest) {

        // 1. 카테고리 ID 유효성 검사
        // 2. 행정구역 ID 유효성 검사 + 인접동 목록 조회
        // 3. 검색 기록 추가 ( USER_ID, DIVISION_ID, DEPTH, CATEGORY_ID, KEYWORD)

        // 4. 검색 (행정구역, 카테고리, 상태, 키워드, 페이징, 정렬)
        //   - 행정구역: 인접동 목록 포함
        //   - 카테고리: categoryId (categoryId)
        //   - 상태: status (기본값 RECRUITING)
        //   - 키워드: keyword (마켓글 제목, 내용)
        //   - 정렬: 최신순 (마켓글 ID 내림차순)
        //   - 페이징: pageNum (기본값 0), pageSize (기본값 20)

        // 5. 작성자 닉네임, 프로필 이미지 URL 조회 (User Service API 호출)
        // 6. 현재 모집 참여 인원 수 조회 (chat Service API 호출)

        // 7. 응답 생성하기

        // 1. 카테고리 ID 유효성 검사
        if (marketListRequest.getCategoryId() != null) {
            categoryService.validateCategoryId(marketListRequest.getCategoryId());
        }

        // 2. 행정구역 ID 유효성 검사 + 인접동 목록 조회
        List<DivisionResponseDto> requestDivisions = null;
        if (marketListRequest.getDepth() == 0) {
            DivisionResponseDto divisionByCode = divisionService.getDivisionByCode(marketListRequest.getDivisionId());
            requestDivisions = List.of(divisionByCode);
        } else {
            requestDivisions = divisionService.getNearDivisionsByCode(marketListRequest.getDepth(), marketListRequest.getDivisionId());
        }

        // 3. 검색 기록 추가 ( USER_ID, DIVISION_ID, DEPTH, CATEGORY_ID, KEYWORD)
        // 카테고리나 키워드 둘 중 하나라도 있으면 검색 기록 저장
        if (requestUserID != null) {
            if (marketListRequest.getCategoryId() != null || (marketListRequest.getKeyword() != null && !marketListRequest.getKeyword().isBlank())) {
                searchService.saveSearchFromRequest(marketListRequest, requestUserID);
            }
        }


        // 4. 검색 (행정구역, 카테고리, 상태, 키워드, 페이징, 정렬)
        Page<Market> markets = marketService.searchMarkets(requestDivisions, marketListRequest);

        // 5. 작성자 닉네임, 프로필 이미지 URL 조회 (User Service API 호출)
        List<Long> authorIds = markets.stream()
                .map(Market::getAuthorId)
                .toList();
        List<UserInternalResponse> users = userService.getUsersByIds(authorIds);

        // 6. 현재 모집 참여 인원 수 조회 (chat Service API 호출) - TODO: 251023 14:00 임시 작성
        List<ParticipantListResponseWrap> participantsWrap = chatService.getParticipantsWrap(markets.getContent().stream().map(Market::getId).toList());


        // 7. 응답 생성하기
        MarketListResponse marketListResponse = MarketListResponse.fromEntities(markets, users, participantsWrap);


        return marketListResponse;
    }

    // 마켓글 취소 - internal
    public void cancelMarketPost(Long requestUserID, Long marketId) {
        // 마켓글 가져오기
        Market market = marketService.findByMarketId(marketId);

        // 권한 확인
        if (!market.getAuthorId().equals(requestUserID)) {
            throw new MarketException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        // 마켓글 상태 확인 ( 모집 완료는 삭제 불가 )
        if (market.getStatus() == MarketStatus.ENDED) {
            throw new MarketException("모집 완료된 마켓글은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        // 마켓글 상태 변경 (취소)
        marketService.changeStatus(market, MarketStatus.CANCELLED);
    }

    // 마켓글 완료 - internal
    public void completeMarketPost(Long requestUserID, Long marketId) {
        // 마켓글 가져오기
        Market market = marketService.findByMarketId(marketId);

        // 권한 확인
        if (!market.getAuthorId().equals(requestUserID)) {
            throw new MarketException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        // 마켓글 상태 확인 ( 모집중 상태만 완료 가능 )
        if (market.getStatus() != MarketStatus.RECRUITING) {
            throw new MarketException("모집중 상태의 마켓글만 완료할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }
        // 마켓글 상태 변경 (완료)
        marketService.changeStatus(market, MarketStatus.ENDED);
    }
}
