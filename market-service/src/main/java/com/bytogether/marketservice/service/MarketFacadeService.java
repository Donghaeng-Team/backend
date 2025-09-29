package com.bytogether.marketservice.service;

import com.bytogether.marketservice.client.DivisionServiceClient;
import com.bytogether.marketservice.client.dto.DivisionResponseDto;
import com.bytogether.marketservice.dto.request.CreateMarketRequest;
import com.bytogether.marketservice.entity.Market;
import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.service.sub.*;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
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
    public void createMarketPost(CreateMarketRequest createMarketRequest) {

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

    }

}
