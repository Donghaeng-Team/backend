package com.bytogether.marketservice.dto.validation.annotation;


import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.dto.validation.validator.MarketStatusSubsetValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * MarketStatus 열거형의 하위 집합인지 검증하는 커스텀 어노테이션
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

@Documented
@Constraint(validatedBy = MarketStatusSubsetValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MarketStatusSubset {
    MarketStatus[] anyOf();

    String message() default "must be any of {anyOf}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
