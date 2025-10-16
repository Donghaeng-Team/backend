package com.bytogether.marketservice.repository.queryDsl;


import com.bytogether.marketservice.client.dto.response.DivisionResponseDto;
import com.bytogether.marketservice.constant.MarketSort;
import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.dto.request.MarketListRequest;
import com.bytogether.marketservice.entity.Market;
import com.bytogether.marketservice.entity.QMarket;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

@RequiredArgsConstructor
public class MarketQueryRepositoryImpl implements MarketQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Market> searchMarkets(List<DivisionResponseDto> requestDivisions, MarketListRequest marketListRequest) {
        // 4. 검색 (행정구역, 카테고리, 상태, 키워드, 페이징, 정렬)
        //   - 행정구역: 인접동 목록 포함  in
        //   - 카테고리: categoryId (categoryId) startsWith
        //   - 상태: status (기본값 RECRUITING) equals
        //   - 키워드: keyword (마켓글 제목, 내용) containsIgnoreCase or containsIgnoreCase
        //   - 정렬: 최신순, 마감임박순, 저렴한순, 조회수순,
        //   - 페이징: pageNum (기본값 0), pageSize (기본값 20)

        QMarket market = QMarket.market;

        BooleanBuilder builder = new BooleanBuilder();

        // 행정구역 조건 추가
        if (requestDivisions != null && !requestDivisions.isEmpty()) {
            List<String> emdCodes = requestDivisions.stream()
                    .map(DivisionResponseDto::getId)
                    .toList();
            builder.and(market.divisionId.in(emdCodes));
        }

        // 카테고리 조건 추가
        if (marketListRequest.getCategoryId() != null) {
            builder.and(market.categoryId.startsWith(marketListRequest.getCategoryId()));
        }

        // 상태 조건 추가
        if (marketListRequest.getStatus() != null && !marketListRequest.getStatus().toString().isBlank()) {
            builder.and(market.status.eq(marketListRequest.getStatus()));
        } else {
            // 기본값 RECRUITING
            builder.and(market.status.eq(MarketStatus.RECRUITING));
        }

        // 키워드 조건 추가
        if (marketListRequest.getKeyword() != null) {
            String keyword = marketListRequest.getKeyword().trim();
            builder.and(
                    market.title.containsIgnoreCase(keyword)
                            .or(market.content.containsIgnoreCase(keyword))
            );
        }

        // 정렬

        OrderSpecifier<?> orderSpecifier = null;

        // null 값 처리
        switch (marketListRequest.getSort() == null ? MarketSort.LATEST : marketListRequest.getSort()) {
            // 최신순 (마켓글 ID 내림차순)
            case LATEST -> orderSpecifier = market.id.desc();

            // 마감임박순 (마감일 오름차순)
            case ENDING_SOON -> orderSpecifier = market.endTime.asc();

            // 저렴한순 (가격 오름차순) - 가격 / 모집인원 수
            case CHEAPEST -> orderSpecifier = Expressions.numberTemplate(
                    Double.class,
                    "({0} / {1})",
                    market.price,
                    market.recruitMax).asc();

            // 조회수순 (조회수 내림차순)
            case MOST_VIEWED -> orderSpecifier = market.views.desc();

            default -> throw new IllegalArgumentException("Invalid sort value: " + marketListRequest.getSort());
        }

        // 페이징
        Pageable pageable = PageRequest.of(marketListRequest.getPageNum(), marketListRequest.getPageSize());

        List<Market> fetch = queryFactory.selectFrom(market)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(market.count())
                .from(market)
                .where(builder)
                .fetchOne();
        if (total == null) total = 0L;

        // 응답 생성 및 반환

        return new PageImpl<>(fetch, pageable, total);
    }

}
