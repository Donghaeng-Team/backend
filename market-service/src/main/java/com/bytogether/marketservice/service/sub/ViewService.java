package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.entity.View;
import com.bytogether.marketservice.repository.MarketRepository;
import com.bytogether.marketservice.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 공동 구매 마켓 상세 조회, 조회수 증가 관련 서비스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Service
@RequiredArgsConstructor
public class ViewService {
    private final ViewRepository viewRepository;
    private final MarketRepository marketRepository;


    // 조회 기록 저장 및 조회수 증가
    public void recordView(Long requestUserID, Long marketId) {
        View view = new View();
        view.setUserId(requestUserID);
        view.setMarketId(marketId);
        viewRepository.save(view);
        incrementViewCount(marketId);
    }

    // 조회수 증가
    public void incrementViewCount(Long marketId) {
        marketRepository.findById(marketId).ifPresent(market -> {
            market.setViews(market.getViews() + 1);
            marketRepository.save(market);
        });
    }
}
