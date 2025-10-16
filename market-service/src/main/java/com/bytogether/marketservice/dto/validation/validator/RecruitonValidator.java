package com.bytogether.marketservice.dto.validation.validator;

import com.bytogether.marketservice.dto.request.CreateMarketRequest;
import com.bytogether.marketservice.dto.validation.annotation.RecruitonMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * CreateMarketRequest의 recruitMin과 recruitMax의 유효성을 검증하는 Validator
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-09-29
 */

// recruitMax always equals or greater than recruitMin
public class RecruitonValidator implements ConstraintValidator<RecruitonMatches, CreateMarketRequest> {
    @Override
    public boolean isValid(CreateMarketRequest request, ConstraintValidatorContext context) {
        if (request.getRecruitMin() == null || request.getRecruitMax() == null) {
            return true; // null 값은 다른 어노테이션에서 처리
        }
        return request.getRecruitMax() >= request.getRecruitMin();
    }
}
