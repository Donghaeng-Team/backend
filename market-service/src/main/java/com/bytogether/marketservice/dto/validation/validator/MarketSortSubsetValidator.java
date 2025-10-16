package com.bytogether.marketservice.dto.validation.validator;

import com.bytogether.marketservice.constant.MarketSort;
import com.bytogether.marketservice.dto.validation.annotation.MarketSortSubset;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * MarketSortSubset Validator
 *
 * @author insu9058@naver.com
 * @version 1.0
 * @since 2025-10-10
 */

public class MarketSortSubsetValidator implements ConstraintValidator<MarketSortSubset, MarketSort> {
    private MarketSort[] subset;

    @Override
    public void initialize(MarketSortSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(MarketSort value, ConstraintValidatorContext context) {
        if (value == null) return true; // null 허용 시
        return Arrays.asList(subset).contains(value);
    }
}
