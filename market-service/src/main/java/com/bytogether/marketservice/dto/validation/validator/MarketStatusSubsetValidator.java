package com.bytogether.marketservice.dto.validation.validator;

import com.bytogether.marketservice.constant.MarketStatus;
import com.bytogether.marketservice.dto.validation.annotation.MarketStatusSubset;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * MarketStatusSubset Validator
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

public class MarketStatusSubsetValidator implements ConstraintValidator<MarketStatusSubset, MarketStatus> {
    private MarketStatus[] subset;

    @Override
    public void initialize(MarketStatusSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(MarketStatus value, ConstraintValidatorContext context) {
        if (value == null) return true; // null 허용 시
        return Arrays.asList(subset).contains(value);
    }
}