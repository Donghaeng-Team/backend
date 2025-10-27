package com.bytogether.userservice.validation;

import com.bytogether.userservice.dto.request.PasswordMatchable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordMatchable> {
    @Override
    public boolean isValid(PasswordMatchable request, ConstraintValidatorContext context)  {
        if (request.getPassword() == null || request.getPasswordConfirm() == null) {
            return false;
        }
        boolean match = request.getPassword().equals(request.getPasswordConfirm());
        if (!match) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("비밀번호가 일치하지 않습니다")
                    .addPropertyNode("passwordConfirm")
                    .addConstraintViolation();
        }
        return match;
    }
}
