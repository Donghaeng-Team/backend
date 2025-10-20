package com.bytogether.marketservice.repository;

import com.bytogether.marketservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
}
