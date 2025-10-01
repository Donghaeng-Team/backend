package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.entity.Market;
import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.repository.MarketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    // 여러 마켓 아이디로 마켓 리스트 조회
    public List<Market> getMarketsByIds(List<Long> list) {
        return marketRepository.findAllById(list);
    }

    public Market findByMarketId(Long marketId) {
        return marketRepository.findById(marketId).orElseThrow(() -> new MarketException("Market not found", HttpStatus.NOT_FOUND));
    }

    public void changeStatus(Market market, MarketStatus marketStatus) {
        market.setStatus(marketStatus);
        marketRepository.save(market);
    }

    // 작성자 ID로 마켓 리스트 조회 (삭제된 마켓 제외)
    public List<Market> getMarketsByAuthorId(Long userId) {
        return marketRepository.findByAuthorIdAndStatusIsNot(userId, MarketStatus.REMOVED);
    }
}
