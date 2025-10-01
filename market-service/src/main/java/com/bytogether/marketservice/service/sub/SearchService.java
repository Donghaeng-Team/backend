package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.entity.Search;
import com.bytogether.marketservice.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


/**
 * 공동 구매 마켓 조회 기록 관련 서비스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;

    // 검색 기록 저장
    @Async
    public void saveSearch(Search newSearch) {
        searchRepository.save(newSearch);
    }

}
