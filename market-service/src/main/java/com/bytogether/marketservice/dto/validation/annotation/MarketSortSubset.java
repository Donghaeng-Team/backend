package com.bytogether.marketservice.dto.validation.annotation;

import com.bytogether.marketservice.constant.MarketSort;
import com.bytogether.marketservice.dto.validation.validator.MarketSortSubsetValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * MarketSort 열거형의 하위 집합인지 검증하는 커스텀 어노테이션
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

@Documented
@Constraint(validatedBy = MarketSortSubsetValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MarketSortSubset {
    MarketSort[] anyOf();

    String message() default "must be any of {anyOf}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
