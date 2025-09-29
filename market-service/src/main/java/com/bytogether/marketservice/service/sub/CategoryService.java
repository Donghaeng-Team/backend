package com.bytogether.marketservice.service.sub;

import com.bytogether.marketservice.exception.MarketException;
import com.bytogether.marketservice.repository.CategoryRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


/**
 * 공동 구매 카테고리 관련 서비스
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public void validateCategoryId(@NotBlank String categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new MarketException("Invalid categoryId: " + categoryId, HttpStatus.BAD_REQUEST));
    }
}
