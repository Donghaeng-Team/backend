package com.bytogether.marketservice.service;


import com.bytogether.marketservice.client.dto.response.ParticipantListResponseWrap;
import com.bytogether.marketservice.client.dto.response.UserInternalResponse;
import com.bytogether.marketservice.dto.request.CartListRequest;
import com.bytogether.marketservice.dto.response.CartResponse;
import com.bytogether.marketservice.dto.response.MarketListResponse;
import com.bytogether.marketservice.entity.Cart;
import com.bytogether.marketservice.entity.Market;
import com.bytogether.marketservice.service.sub.CartService;
import com.bytogether.marketservice.service.sub.ChatService;
import com.bytogether.marketservice.service.sub.MarketService;
import com.bytogether.marketservice.service.sub.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 공동 구매 찜하기 관련 서비스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Service
@RequiredArgsConstructor
@Transactional
public class CartFacadeService {
    private final CartService cartService;
    private final MarketService marketService;

    private final UserService userService;
    private final ChatService chatService;

    // 찜하기 추가
    public CartResponse addCart(Long requestUserID, Long marketId) {
        // 찜하기 추가
        Cart cart = cartService.addCart(requestUserID, marketId);

        // 응답 반환
        return new CartResponse(cart.getId());
    }

    // 찜하기 삭제
    public void deleteCart(Long requestUserID, Long marketId) {
        //  찜하기 삭제
        cartService.deleteCart(requestUserID, marketId);
    }

    // 내가 찜한 목록 보기
    public MarketListResponse getMyCarts(Long requestUserID, @Valid CartListRequest cartListRequest) {
        // 찜하기 목록 조회
        Page<Cart> myCarts = cartService.getMyCarts(requestUserID,
                PageRequest.of(cartListRequest.getPageNum(), cartListRequest.getPageSize())
        );

        // 마켓 정보 조회
        List<Market> markets = marketService.getMarketsByIds(myCarts.getContent().stream().map(Cart::getMarketId).toList());

        // 마켓 작성자 정보 조회
        List<Long> authorIds = markets.stream().map(Market::getAuthorId).distinct().toList();
        List<UserInternalResponse> authors = userService.getUsersByIds(authorIds);

        List<ParticipantListResponseWrap> participantsWrap = chatService.getParticipantsWrap(markets.stream().map(Market::getId).toList());

        MarketListResponse marketListResponse = MarketListResponse.fromEntities(new PageImpl<>(markets, myCarts.getPageable(), myCarts.getTotalElements()), authors, participantsWrap);

        return marketListResponse;
    }
}
