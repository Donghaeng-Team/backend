package com.bytogether.marketservice.repository;

import com.bytogether.marketservice.constant.CartStatus;
import com.bytogether.marketservice.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Page<Cart> findByUserIdAndStatus(Long userId, CartStatus status, Pageable pageable);

    List<Cart> findByMarketIdAndStatus(Long marketId, CartStatus status);

    Optional<Cart> findByUserIdAndMarketIdAndStatus(Long userId, Long marketId, CartStatus status);

    Long countByUserIdAndStatus(Long userId, CartStatus status);
}
