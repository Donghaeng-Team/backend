package com.bytogether.marketservice.controller;


import com.bytogether.marketservice.dto.request.CreateMarketRequest;
import com.bytogether.marketservice.dto.request.ExtendMarketRequest;
import com.bytogether.marketservice.dto.request.PutMarketRequest;
import com.bytogether.marketservice.dto.response.CreateMarketResponse;
import com.bytogether.marketservice.dto.response.ExtendMarketResponse;
import com.bytogether.marketservice.dto.response.PutMarketResponse;
import com.bytogether.marketservice.service.MarketFacadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 공동 구매 마켓 관련 API 컨트롤러
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@RestController
@RequestMapping("/api/v1/market/private")
@RequiredArgsConstructor
public class MarketController {
    private final MarketFacadeService marketFacadeService;

//    1.마켓글 작성 createMarketPost - private (완료)
//    2.마켓글 수정 updateMarketPost - private (완료) - 프론트에서 이미지 관리 필요(기존 이미지 유지, 삭제, 추가, 변경 등) // 모든 이미지 교체로 구현
//    3.마켓글 삭제 (취소) deleteMarketPost - private (완료)
//    4.마켓글 연장 extendMarketPost - private
//    5.마켓글 목록 조회 (필터링, 페이징) (로그인) - getMarketPostsWithLogin - private
//    6.마켓글 상세 조회 (로그인) - getMarketPostDetailWithLogin - private
//    7.마켓글 상태 변경 (모집중, 거래완료, 취소 등) changeMarketPostStatus - private

    // 1. 마켓글 작성 createMarketPost - private
    // multipart/form-data 형식으로 요청 처리
    // 이미지 파일 업로드를 위해 @ModelAttribute 사용
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CreateMarketResponse> createMarketPost(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @Valid @ModelAttribute CreateMarketRequest createMarketRequest) {
        CreateMarketResponse marketPost = marketFacadeService.createMarketPost(requestUserID, createMarketRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(marketPost);
    }

    // 2. 마켓글 수정 updateMarketPost - private
    @PutMapping(path = "/{marketId}",consumes = "multipart/form-data")
    public ResponseEntity<PutMarketResponse> updateMarketPost(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @PathVariable Long marketId, @Valid @ModelAttribute PutMarketRequest putMarketRequest) {
        PutMarketResponse putMarketResponse = marketFacadeService.updateMarketPost(requestUserID, marketId, putMarketRequest);

        return ResponseEntity.status(HttpStatus.OK).body(putMarketResponse);
    }

    // 3. 마켓글 삭제 (취소) deleteMarketPost - private
    // TODO: 실제로는 삭제가 아닌 취소
    @DeleteMapping("/{marketId}")
    public ResponseEntity<?> deleteMarketPost(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @PathVariable Long marketId) {
        marketFacadeService.deleteMarketPost(requestUserID, marketId);

        return ResponseEntity.status(HttpStatus.OK).body("Market post " + marketId + " deleted");
    }

    // 4. 마켓글 연장 extendMarketPost - private
    @PatchMapping("/extend/{marketId}")
    public ResponseEntity<ExtendMarketResponse> extendMarketPost(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @PathVariable Long marketId, @RequestBody ExtendMarketRequest extendMarketRequest) {
        ExtendMarketResponse extendMarketResponse = marketFacadeService.extendMarketPost(requestUserID, marketId, extendMarketRequest);

        return ResponseEntity.status(HttpStatus.OK).body(extendMarketResponse);
    }


    // 5. 마켓글 목록 조회 (필터링, 페이징) (로그인) - getMarketPostsWithLogin - private
    @GetMapping
    public String getMarketPostsWithLogin(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID) {
        return "List of market posts for logged-in user";
    }


    // 6. 마켓글 상세 조회 (로그인) - getMarketPostDetailWithLogin - private
    @GetMapping("/{marketId}")
    public String getMarketPostDetailWithLogin(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @PathVariable Long marketId) {
        marketFacadeService.getMarketPostDetailWithLogin(requestUserID, marketId);
        return "Details of market post " + marketId + " for logged-in user";
    }

    // 7. 마켓글 상태 변경 (모집중, 거래완료, 취소 등) changeMarketPostStatus - private
    @PatchMapping("/status/{marketId}")
    public String changeMarketPostStatus(@RequestHeader(value = "X-User-Id", required = true) Long requestUserID, @PathVariable Long marketId, @RequestParam String status) {
        return "Market post " + marketId + " status changed to " + status;
    }


}
