package com.bytogether.marketservice.repository;

import com.bytogether.marketservice.constant.CartStatus;
import com.bytogether.marketservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserIdAndStatus(Long userId, CartStatus status);

    List<Cart> findByMarketIdAndStatus(Long marketId, CartStatus status);
}
