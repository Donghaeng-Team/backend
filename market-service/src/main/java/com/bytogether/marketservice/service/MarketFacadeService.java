package com.bytogether.marketservice.service;

import com.bytogether.marketservice.client.DivisionServiceClient;
import com.bytogether.marketservice.client.dto.DivisionResponseDto;
import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.dto.request.CreateMarketRequest;
import com.bytogether.marketservice.dto.response.CreateMarketResponse;
import com.bytogether.marketservice.entity.Market;
import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.service.sub.*;
import jakarta.transaction.Transactional;
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

    // 마켓글 작성
    public CreateMarketResponse createMarketPost(CreateMarketRequest createMarketRequest) {

        // 1. 로그인 사용자 확인 (추후 구현)
        Long userId = 1L; // TODO: 실제 로그인 사용자 ID로 대체

        // 2. categoryId 유효성 검사
        categoryService.validateCategoryId(createMarketRequest.getCategoryId());

        // 3. 좌표 기반 행정구역 조회
        DivisionResponseDto division =
                divisionServiceClient.getDivisionByCoord(createMarketRequest.getLatitude().doubleValue(), createMarketRequest.getLongitude().doubleValue())
                        .orElseThrow(() -> new MarketException("Invalid coordinates: no division found", HttpStatus.BAD_REQUEST));

        // 4. 이미지 파일 MIME 타입 검사
        imageService.isAllowedMimeType(createMarketRequest.getImages());

        // 5. 마켓글 생성
        Market newMarket = Market.fromCreateRequest(createMarketRequest, userId, division.getEmdName(),division.getId());

        Market savedMarket = marketService.createMarket(newMarket);


        // 6. 이미지 파일 처리
        List<MultipartFile> images = createMarketRequest.getImages();
        imageService.handleImageWhenMarketCreate(images, savedMarket.getId());


        return CreateMarketResponse.fromEntity(savedMarket);
    }

    // 마켓글 삭제 (취소)
    public void deleteMarketPost(Long marketId) {
        // 1. 유저 권한 확인 (추후 구현)
        Long userId = 1L; // TODO: 실제 로그인 사용자 ID로 대체

        // 2. 마켓글 가져오기
        Market market = marketService.findByMarketId(marketId);

        // 3. 권한 확인
        if (!market.getAuthorId().equals(userId)) {
            throw new MarketException("권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 4. 마켓글 상태 변경 (취소)
        marketService.changeStatus(market, MarketStatus.REMOVED);

        // 5. 응답 반환 (추후 구현)
    }

    // 자신의 마켓글 조회
    public void getMyMarketPosts() {
        // 1. 로그인 사용자 확인 (추후 구현)
        Long userId = 1L; // TODO: 실제 로그인 사용자 ID로 대체

        // 2. 자신의 마켓글 조회
        List<Market> myMarkets = marketService.getMarketsByAuthorId(userId);

        // 3. 응답 반환 (추후 구현)
    }
}
