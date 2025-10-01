package com.bytogether.marketservice.dto.validation.annotation;

import com.bytogether.marketservice.dto.validation.validator.RecruitonValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

/**
 * CreateMarketRequest의 recruitMin과 recruitMax의 유효성을 검증하는 어노테이션
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

@Documented
@Constraint(validatedBy = RecruitonValidator.class)
@Target(ElementType.TYPE)  // 클래스 단위!
@Retention(RetentionPolicy.RUNTIME)
public @interface RecruitonMatches {
    String message() default "최대 모집 인원은 최소 모집 인원보다 크거나 같아야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
