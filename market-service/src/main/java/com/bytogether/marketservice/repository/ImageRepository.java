package com.bytogether.marketservice.repository;

import com.bytogether.marketservice.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByMarketId(Long marketId);
}
