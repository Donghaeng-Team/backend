package com.bytogether.marketservice.repository;

import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.entity.Market;
import com.bytogether.marketservice.repository.queryDsl.MarketQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Repository
public interface MarketRepository extends JpaRepository<Market, Long>, MarketQueryRepository {

    Page<Market> findByAuthorIdAndStatusIsNot(Long authorId, MarketStatus status, Pageable pageable);

    Page<Market> findAllByIdIn(List<Long> ongoing, PageRequest pageRequest);
}
