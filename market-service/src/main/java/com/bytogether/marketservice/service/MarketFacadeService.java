package com.bytogether.marketservice.service;

import com.bytogether.marketservice.service.sub.ImageService;
import com.bytogether.marketservice.service.sub.MarketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final MarketService marketService;
    private final ImageService imageService;
}
