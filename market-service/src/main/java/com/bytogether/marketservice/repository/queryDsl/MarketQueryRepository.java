package com.bytogether.marketservice.repository.queryDsl;

import com.bytogether.marketservice.client.dto.response.DivisionResponseDto;
import com.bytogether.marketservice.dto.request.MarketListRequest;
import com.bytogether.marketservice.entity.Market;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

public interface MarketQueryRepository {
    Page<Market> searchMarkets(List<DivisionResponseDto> requestDivisions, MarketListRequest marketListRequest);
}
