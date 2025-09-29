package com.bytogether.marketservice.repository;

import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
    List<Market> findByAuthorIdAndStatusIsNot(Long authorId, MarketStatus status);
}
