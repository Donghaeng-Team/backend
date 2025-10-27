package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.client.dto.response.DivisionResponseDto;
import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.dto.request.MarketListRequest;
import com.bytogether.marketservice.entity.Market;
import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.repository.MarketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 공동 구매 마켓 관련 서비스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-26
 */

@Service
@RequiredArgsConstructor
public class MarketService {
    private final MarketRepository marketRepository;

    @Transactional
    public Market saveMarket(Market newMarket) {
        return marketRepository.save(newMarket);
    }

    // 여러 마켓 아이디로 마켓 리스트 조회 페이징
    public List<Market> getMarketsByIds(List<Long> list) {
        return marketRepository.findAllById(list);
    }

    // 마켓 아이디로 마켓 단건 조회
    public Market findByMarketId(Long marketId) {
        return marketRepository.findById(marketId).orElseThrow(() -> new MarketException("Market not found", HttpStatus.NOT_FOUND));
    }

    // 작성자 ID로 마켓 리스트 조회 (삭제된 마켓 제외)
    public Page<Market> getMarketsByAuthorId(Long userId, Pageable pageable) {
        return marketRepository.findByAuthorIdAndStatusIsNot(userId, MarketStatus.REMOVED, pageable);
    }

    // 마켓 상태 변경
    public void changeStatus(Market market, MarketStatus marketStatus) {
        market.setStatus(marketStatus);
        marketRepository.save(market);
    }

    // 마켓 검색 (필터링, 페이징) - QueryDSL 사용
    public Page<Market> searchMarkets(List<DivisionResponseDto> requestDivisions, MarketListRequest marketListRequest) {

        return marketRepository.searchMarkets(requestDivisions, marketListRequest);
    }

    public Page<Market> getMarketsByIds(List<Long> ongoing, PageRequest pageRequest) {
        Page<Market> allByIdIn = marketRepository.findAllByIdIn(ongoing, pageRequest);
        return allByIdIn;
    }
}
