package com.bytogether.marketservice.service;

import com.bytogether.marketservice.client.DivisionServiceClient;
import com.bytogether.marketservice.client.dto.response.DivisionResponseDto;
import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.dto.request.CreateMarketRequest;
import com.bytogether.marketservice.dto.request.ExtendMarketRequest;
import com.bytogether.marketservice.dto.request.PutMarketRequest;
import com.bytogether.marketservice.dto.response.CreateMarketResponse;
import com.bytogether.marketservice.dto.response.ExtendMarketResponse;
import com.bytogether.marketservice.dto.response.PutMarketResponse;
import com.bytogether.marketservice.entity.Market;
import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.service.sub.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final CartService cartService;
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final MarketService marketService;
    private final SearchService searchService;
    private final ViewService viewService;
    private final DivisionServiceClient divisionServiceClient;

    // 마켓글 작성 - private
    public CreateMarketResponse createMarketPost(Long requestUserID, CreateMarketRequest createMarketRequest) {

        // categoryId 유효성 검사
        categoryService.validateCategoryId(createMarketRequest.getCategoryId());

        // 좌표 기반 행정구역 조회
        DivisionResponseDto division =
                divisionServiceClient.getDivisionByCoord(createMarketRequest.getLatitude().doubleValue(), createMarketRequest.getLongitude().doubleValue())
                        .orElseThrow(() -> new MarketException("Invalid coordinates: no division found", HttpStatus.BAD_REQUEST));

        // 이미지 파일 MIME 타입 검사
        imageService.isAllowedMimeType(createMarketRequest.getImages());

        // 마켓글 생성
        Market newMarket = Market.fromCreateRequest(createMarketRequest, requestUserID, division.getEmdName(),division.getId());

        Market savedMarket = marketService.saveMarket(newMarket);


        // 이미지 파일 처리
        List<MultipartFile> images = createMarketRequest.getImages();
        imageService.handleImageWhenMarketCreate(images, savedMarket.getId());


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

        // 마켓글 상태 변경 (취소)
        marketService.changeStatus(market, MarketStatus.REMOVED);

        // 응답 반환 (추후 구현)
    }

    // 특정 사용자가 작성한 마켓글 조회
    public void getMyMarketPosts(Long targetUserId) {

        // 특정 사용자가 작성한 마켓글 조회
        List<Market> myMarkets = marketService.getMarketsByAuthorId(targetUserId);

        // 응답 반환 (추후 구현)
    }

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

    public void getMarketPostDetailWithLogin(Long requestUserID, Long marketId) {
        Market byMarketId = marketService.findByMarketId(marketId);
        viewService.recordView(requestUserID, marketId);

    }
}
